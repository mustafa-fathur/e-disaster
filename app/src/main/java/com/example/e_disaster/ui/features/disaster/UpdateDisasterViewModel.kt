package com.example.e_disaster.ui.features.disaster

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.disaster.UpdateDisasterRequest
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class UpdateDisasterUiState(
    val disasterId: String = "",
    val title: String = "",
    val description: String = "",
    val selectedType: String = "earthquake",
    val selectedTypeLabel: String = "Gempa Bumi",
    val selectedStatus: String = "ongoing",
    val selectedStatusLabel: String = "Berlangsung",
    val location: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val date: String = "",
    val time: String = "",
    val magnitude: String = "",
    val depth: String = "",
    val images: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingData: Boolean = true,
    val errorMessage: String? = null
)

sealed class UpdateDisasterEvent {
    data class Success(val message: String) : UpdateDisasterEvent()
    data class Error(val message: String) : UpdateDisasterEvent()
}

@HiltViewModel
class UpdateDisasterViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf(UpdateDisasterUiState())
        private set

    private val _events = MutableSharedFlow<UpdateDisasterEvent>()
    val events = _events.asSharedFlow()

    fun loadDisaster(disasterId: String) {
        uiState = uiState.copy(disasterId = disasterId, isLoadingData = true)
        
        viewModelScope.launch {
            try {
                val disaster = disasterRepository.getDisasterById(disasterId)
                uiState = uiState.copy(
                    title = disaster.title ?: "",
                    description = disaster.description ?: "",
                    selectedType = disaster.types ?: "earthquake",
                    selectedTypeLabel = getTypeLabel(disaster.types ?: "earthquake"),
                    selectedStatus = disaster.status ?: "ongoing",
                    selectedStatusLabel = getStatusLabel(disaster.status ?: "ongoing"),
                    location = disaster.location ?: "",
                    latitude = disaster.lat?.toString() ?: "",
                    longitude = disaster.long?.toString() ?: "",
                    date = disaster.date ?: "",
                    time = disaster.time?.take(5) ?: "",
                    magnitude = disaster.magnitude?.toString() ?: "",
                    depth = disaster.depth?.toString() ?: "",
                    isLoadingData = false
                )
            } catch (e: Exception) {
                _events.emit(UpdateDisasterEvent.Error("Gagal memuat data bencana: ${e.message}"))
                uiState = uiState.copy(isLoadingData = false)
            }
        }
    }

    private fun getTypeLabel(type: String): String {
        return when (type) {
            "earthquake" -> "Gempa Bumi"
            "tsunami" -> "Tsunami"
            "volcanic_eruption" -> "Gunung Meletus"
            "flood" -> "Banjir"
            "drought" -> "Kekeringan"
            "tornado" -> "Angin Topan"
            "landslide" -> "Tanah Longsor"
            "non_natural_disaster" -> "Bencana Non Alam"
            "social_disaster" -> "Bencana Sosial"
            else -> "Lainnya"
        }
    }

    private fun getStatusLabel(status: String): String {
        return when (status) {
            "ongoing" -> "Berlangsung"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            else -> "Tidak Diketahui"
        }
    }

    fun updateTitle(value: String) {
        uiState = uiState.copy(title = value)
    }

    fun updateDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun updateType(typeValue: String, typeLabel: String) {
        uiState = uiState.copy(selectedType = typeValue, selectedTypeLabel = typeLabel)
    }

    fun updateStatus(statusValue: String, statusLabel: String) {
        uiState = uiState.copy(selectedStatus = statusValue, selectedStatusLabel = statusLabel)
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

    fun submitUpdate() {
        if (uiState.title.isBlank()) {
            viewModelScope.launch {
                _events.emit(UpdateDisasterEvent.Error("Judul bencana harus diisi"))
            }
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val request = UpdateDisasterRequest(
                    title = uiState.title,
                    description = uiState.description.ifBlank { null },
                    type = uiState.selectedType,
                    status = uiState.selectedStatus,
                    date = uiState.date.ifBlank { null },
                    time = if (uiState.time.isNotBlank()) {
                        if (uiState.time.length == 5) "${uiState.time}:00" else uiState.time
                    } else null,
                    location = uiState.location.ifBlank { null },
                    lat = uiState.latitude.toDoubleOrNull(),
                    long = uiState.longitude.toDoubleOrNull(),
                    magnitude = uiState.magnitude.toDoubleOrNull(),
                    depth = uiState.depth.toDoubleOrNull()
                )

                val response = disasterRepository.updateDisaster(uiState.disasterId, request)
                
                // Upload gambar baru jika ada
                if (uiState.images.isNotEmpty()) {
                    try {
                        uiState.images.forEach { uri ->
                            val part = prepareImagePart(uri)
                            if (part != null) {
                                disasterRepository.uploadDisasterImage(uiState.disasterId, part)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                _events.emit(UpdateDisasterEvent.Success(
                    message = response.message ?: "Bencana berhasil diperbarui"
                ))
            } catch (e: Exception) {
                _events.emit(UpdateDisasterEvent.Error("Gagal memperbarui bencana: ${e.message}"))
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
}
