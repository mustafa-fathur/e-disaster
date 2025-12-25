package com.example.e_disaster.ui.features.disaster_report

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.repository.DisasterReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AddReportFormState(
    val title: String = "",
    val description: String = "",
    val isFinal: Boolean = false,
    val imageUris: List<Uri> = emptyList(),
    val lat: String = "",
    val long: String = "",
    val titleError: String? = null,
    val descriptionError: String? = null,
    val latError: String? = null,
    val longError: String? = null
)

data class AddReportRequestState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddDisasterReportViewModel @Inject constructor(
    private val disasterReportRepository: DisasterReportRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var formState by mutableStateOf(AddReportFormState())
        private set

    var requestState by mutableStateOf(AddReportRequestState())
        private set

    fun onTitleChange(title: String) {
        formState = formState.copy(title = title, titleError = null)
    }

    fun onDescriptionChange(description: String) {
        formState = formState.copy(description = description, descriptionError = null)
    }

    fun onIsFinalChange(isFinal: Boolean) {
        formState = formState.copy(isFinal = isFinal)
    }

    fun onLatChange(lat: String) {
        formState = formState.copy(lat = lat, latError = null)
    }

    fun onLongChange(long: String) {
        formState = formState.copy(long = long, longError = null)
    }

    fun addImageUris(uris: List<Uri>) {
        formState = formState.copy(imageUris = formState.imageUris + uris)
    }

    fun removeImageUri(uri: Uri) {
        formState = formState.copy(imageUris = formState.imageUris - uri)
    }

    val canSave: Boolean
        get() = !requestState.isLoading

    private fun validateInput(): Boolean {
        var hasError = false
        var tempState = formState.copy(
            titleError = null,
            descriptionError = null,
            latError = null,
            longError = null
        )

        if (formState.title.isBlank()) {
            tempState = tempState.copy(titleError = "Judul laporan tidak boleh kosong.")
            hasError = true
        }

        if (formState.description.isBlank()) {
            tempState = tempState.copy(descriptionError = "Deskripsi tidak boleh kosong.")
            hasError = true
        }

        if (formState.lat.isNotBlank()) {
            val latValue = formState.lat.toDoubleOrNull()
            if (latValue == null) {
                tempState = tempState.copy(latError = "Latitude harus berupa angka.")
                hasError = true
            } else if (latValue !in -90.0..90.0) {
                tempState = tempState.copy(latError = "Latitude harus antara -90 dan 90.")
                hasError = true
            }
        }

        if (formState.long.isNotBlank()) {
            val longValue = formState.long.toDoubleOrNull()
            if (longValue == null) {
                tempState = tempState.copy(longError = "Longitude harus berupa angka.")
                hasError = true
            } else if (longValue !in -180.0..180.0) {
                tempState = tempState.copy(longError = "Longitude harus antara -180 dan 180.")
                hasError = true
            }
        }

        formState = tempState
        return !hasError
    }

    fun createReport(disasterId: String?) {
        if (!validateInput()) return

        if (disasterId.isNullOrBlank()) {
            requestState = requestState.copy(errorMessage = "ID bencana tidak tersedia. Tidak dapat menyimpan.")
            return
        }

        viewModelScope.launch {
            requestState = requestState.copy(isLoading = true, errorMessage = null, isSuccess = false)
            try {
                val imageFiles = formState.imageUris.mapNotNull { uriToFile(context, it) }

                disasterReportRepository.createDisasterReport(
                    disasterId = disasterId,
                    title = formState.title,
                    description = formState.description,
                    images = imageFiles,
                    isFinalStage = formState.isFinal,
                    lat = formState.lat.toDoubleOrNull(),
                    long = formState.long.toDoubleOrNull()
                )
                requestState = requestState.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                requestState = requestState.copy(isLoading = false, errorMessage = "Gagal menyimpan laporan: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}_${uri.lastPathSegment}")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
