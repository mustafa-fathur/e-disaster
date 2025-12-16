package com.example.e_disaster.ui.features.disaster_victim.add

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.repository.DisasterVictimRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddVictimUiState(
    val name: String = "",
    val nik: String = "",
    val dob: String = "",
    val gender: String = "Laki-laki",
    val contact: String = "",
    val description: String = "",
    val victimStatus: String = "",
    val isEvacuated: Boolean = false,
    val images: List<Uri> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationError: String? = null
)

@HiltViewModel
class AddDisasterVictimViewModel @Inject constructor(
    private val repository: DisasterVictimRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVictimUiState())
    val uiState: StateFlow<AddVictimUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddVictimFormEvent) {
        when (event) {
            is AddVictimFormEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is AddVictimFormEvent.NikChanged -> _uiState.update { it.copy(nik = event.nik) }
            is AddVictimFormEvent.DobChanged -> _uiState.update { it.copy(dob = event.dob) }
            is AddVictimFormEvent.GenderChanged -> _uiState.update { it.copy(gender = event.gender) }
            is AddVictimFormEvent.ContactChanged -> _uiState.update { it.copy(contact = event.contact) }
            is AddVictimFormEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is AddVictimFormEvent.StatusChanged -> _uiState.update { it.copy(victimStatus = event.status) }
            is AddVictimFormEvent.IsEvacuatedChanged -> _uiState.update { it.copy(isEvacuated = event.isEvacuated) }
            is AddVictimFormEvent.ImagesAdded -> _uiState.update { it.copy(images = _uiState.value.images + event.uris) }
            is AddVictimFormEvent.ImageRemoved -> _uiState.update { it.copy(images = _uiState.value.images - event.uri) }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        if (state.name.isBlank() || state.nik.isBlank() || state.dob.isBlank() || state.victimStatus.isBlank()) {
            _uiState.update { it.copy(validationError = "Nama, NIK, Tanggal Lahir, dan Status Korban tidak boleh kosong.") }
            return false
        }
        _uiState.update { it.copy(validationError = null) }
        return true
    }

    fun submitForm(disasterId: String, context: Context) {
        if (!validateForm()) return

        val currentState = _uiState.value
        Log.d("AddVictimLog", "--- Submitting Form ---")
        Log.d("AddVictimLog", "Disaster ID: $disasterId")
        Log.d("AddVictimLog", "NIK: ${currentState.nik}")
        Log.d("AddVictimLog", "Name: ${currentState.name}")
        Log.d("AddVictimLog", "Date of Birth (UI): ${currentState.dob}")
        Log.d("AddVictimLog", "Gender (UI): ${currentState.gender}")
        Log.d("AddVictimLog", "Victim Status (UI): ${currentState.victimStatus}")
        Log.d("AddVictimLog", "Is Evacuated: ${currentState.isEvacuated}")
        Log.d("AddVictimLog", "Contact: ${currentState.contact}")
        Log.d("AddVictimLog", "Description: ${currentState.description}")
        Log.d("AddVictimLog", "Image count: ${currentState.images.size}")
        currentState.images.forEachIndexed { index, uri ->
            Log.d("AddVictimLog", "  Image $index URI: $uri")
        }
        Log.d("AddVictimLog", "--------------------------")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.addDisasterVictim(
                    disasterId = disasterId,
                    uiState = currentState,
                    context = context
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal menambahkan data: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }
}

sealed interface AddVictimFormEvent {
    data class NameChanged(val name: String) : AddVictimFormEvent
    data class NikChanged(val nik: String) : AddVictimFormEvent
    data class DobChanged(val dob: String) : AddVictimFormEvent
    data class GenderChanged(val gender: String) : AddVictimFormEvent
    data class ContactChanged(val contact: String) : AddVictimFormEvent
    data class DescriptionChanged(val description: String) : AddVictimFormEvent
    data class StatusChanged(val status: String) : AddVictimFormEvent
    data class IsEvacuatedChanged(val isEvacuated: Boolean) : AddVictimFormEvent
    data class ImagesAdded(val uris: List<Uri>) : AddVictimFormEvent
    data class ImageRemoved(val uri: Uri) : AddVictimFormEvent
}