package com.example.e_disaster.ui.features.disaster

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.disaster.CreateDisasterRequest
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class AddDisasterUiState(
    val title: String = "",
    val description: String = "",
    val selectedType: String = "earthquake",
    val selectedTypeLabel: String = "Gempa Bumi",
    val location: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val date: String = "",
    val time: String = "",
    val magnitude: String = "",
    val depth: String = "",
    val images: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddDisasterEvent {
    data class Success(val message: String, val disasterId: String) : AddDisasterEvent()
    data class Error(val message: String) : AddDisasterEvent()
}

@HiltViewModel
class AddDisasterViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf(AddDisasterUiState())
        private set

    private val _events = MutableSharedFlow<AddDisasterEvent>()
    val events = _events.asSharedFlow()

    fun updateTitle(value: String) {
        uiState = uiState.copy(title = value)
    }

    fun updateDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun updateType(typeValue: String, typeLabel: String) {
        uiState = uiState.copy(selectedType = typeValue, selectedTypeLabel = typeLabel)
    }

    fun updateLocation(value: String) {
        uiState = uiState.copy(location = value)
    }

    fun updateLatitude(value: String) {
        uiState = uiState.copy(latitude = value)
    }

    fun updateLongitude(value: String) {
        uiState = uiState.copy(longitude = value)
    }

    fun updateDate(value: String) {
        uiState = uiState.copy(date = value)
    }

    fun updateTime(value: String) {
        uiState = uiState.copy(time = value)
    }

    fun updateMagnitude(value: String) {
        uiState = uiState.copy(magnitude = value)
    }

    fun updateDepth(value: String) {
        uiState = uiState.copy(depth = value)
    }

    fun addImages(uris: List<Uri>) {
        uiState = uiState.copy(images = uiState.images + uris)
    }

    fun removeImage(uri: Uri) {
        uiState = uiState.copy(images = uiState.images.filter { it != uri })
    }

    fun submitDisaster() {
        if (uiState.title.isBlank()) {
            viewModelScope.launch {
                _events.emit(AddDisasterEvent.Error("Judul bencana harus diisi"))
            }
            return
        }

        if (uiState.date.isBlank()) {
            viewModelScope.launch {
                _events.emit(AddDisasterEvent.Error("Tanggal harus diisi"))
            }
            return
        }

        if (uiState.time.isBlank()) {
            viewModelScope.launch {
                _events.emit(AddDisasterEvent.Error("Waktu harus diisi"))
            }
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val request = CreateDisasterRequest(
                    title = uiState.title,
                    description = uiState.description.ifBlank { null },
                    source = "manual",
                    type = uiState.selectedType,
                    date = uiState.date,
                    time = if (uiState.time.length == 5) "${uiState.time}:00" else uiState.time,
                    location = uiState.location.ifBlank { null },
                    lat = uiState.latitude.toDoubleOrNull(),
                    long = uiState.longitude.toDoubleOrNull(),
                    magnitude = uiState.magnitude.toDoubleOrNull(),
                    depth = uiState.depth.toDoubleOrNull()
                )

                val response = disasterRepository.createDisaster(request)
                val disasterId = response.data?.id ?: ""
                
                // Upload gambar jika ada
                if (disasterId.isNotEmpty() && uiState.images.isNotEmpty()) {
                    try {
                        uiState.images.forEach { uri ->
                            val part = prepareImagePart(uri)
                            if (part != null) {
                                disasterRepository.uploadDisasterImage(disasterId, part)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Kita tetap anggap sukses membuat bencana, tapi log error gambar
                        // Bisa juga tambahkan info ke user
                    }
                }
                
                _events.emit(AddDisasterEvent.Success(
                    message = response.message ?: "Bencana berhasil ditambahkan",
                    disasterId = disasterId
                ))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBody) ?: "Terjadi kesalahan server: ${e.message()}"
                _events.emit(AddDisasterEvent.Error(errorMessage))
            } catch (e: Exception) {
                _events.emit(AddDisasterEvent.Error("Gagal menambahkan bencana: ${e.message}"))
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            
            // Buat file temporary
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrEmpty()) return null
        return try {
            val jsonObject = JSONObject(errorBody)
            
            // Cek apakah ada field "errors" (format validasi Laravel biasanya)
            if (jsonObject.has("errors")) {
                val errorsObject = jsonObject.getJSONObject("errors")
                val sb = StringBuilder()
                val keys = errorsObject.keys()
                
                while (keys.hasNext()) {
                    val key = keys.next()
                    val errorArray = errorsObject.getJSONArray(key)
                    for (i in 0 until errorArray.length()) {
                        sb.append(errorArray.getString(i)).append("\n")
                    }
                }
                if (sb.isNotEmpty()) return sb.toString().trim()
            }
            
            // Fallback ke field "message"
            if (jsonObject.has("message")) {
                return jsonObject.getString("message")
            }
            
            null
        } catch (e: Exception) {
            // Jika gagal parsing JSON, return null agar fallback ke pesan default
            null
        }
    }
}
