package com.example.e_disaster.ui.features.disaster_victim.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.repository.DisasterVictimRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class UpdateVictimUiState(
    val name: String = "",
    val nik: String = "",
    val dob: String = "",
    val gender: String = "Laki-laki",
    val contact: String = "",
    val description: String = "",
    val victimStatus: String = "",
    val isEvacuated: Boolean = false,

    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationError: String? = null
)

@HiltViewModel
class UpdateDisasterVictimViewModel @Inject constructor(
    private val repository: DisasterVictimRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateVictimUiState())
    val uiState: StateFlow<UpdateVictimUiState> = _uiState.asStateFlow()

    fun loadInitialData(disasterId: String, victimId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val victim = repository.getDisasterVictimDetail(disasterId, victimId)

                val dobUiFormat = try {
                    val apiDate =
                        LocalDate.parse(victim.dateOfBirth.substring(0, 10))
                    apiDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    victim.dateOfBirth
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = victim.name,
                        nik = victim.nik,
                        dob = dobUiFormat,
                        gender = victim.gender,
                        contact = victim.contactInfo,
                        description = victim.description,
                        victimStatus = when (victim.status) {
                            "minor_injury" -> "Luka Ringan"
                            "serious_injuries" -> "Luka Berat"
                            "deceased" -> "Meninggal"
                            "lost" -> "Hilang"
                            else -> victim.status
                        },
                        isEvacuated = victim.isEvacuated
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data korban: ${e.message}"
                    )
                }
            }
        }
    }

    fun onEvent(event: UpdateVictimFormEvent) {
        when (event) {
            is UpdateVictimFormEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is UpdateVictimFormEvent.NikChanged -> _uiState.update { it.copy(nik = event.nik) }
            is UpdateVictimFormEvent.DobChanged -> _uiState.update { it.copy(dob = event.dob) }
            is UpdateVictimFormEvent.GenderChanged -> _uiState.update { it.copy(gender = event.gender) }
            is UpdateVictimFormEvent.ContactChanged -> _uiState.update { it.copy(contact = event.contact) }
            is UpdateVictimFormEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is UpdateVictimFormEvent.StatusChanged -> _uiState.update { it.copy(victimStatus = event.status) }
            is UpdateVictimFormEvent.IsEvacuatedChanged -> _uiState.update { it.copy(isEvacuated = event.isEvacuated) }
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

    fun submitUpdate(disasterId: String, victimId: String) {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            try {
                repository.updateDisasterVictim(
                    disasterId = disasterId,
                    victimId = victimId,
                    uiState = _uiState.value
                )
                _uiState.update { it.copy(isUpdating = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        errorMessage = "Gagal memperbarui data: ${e.message}"
                    )
                }
            }
        }
    }
}

sealed interface UpdateVictimFormEvent {
    data class NameChanged(val name: String) : UpdateVictimFormEvent
    data class NikChanged(val nik: String) : UpdateVictimFormEvent
    data class DobChanged(val dob: String) : UpdateVictimFormEvent
    data class GenderChanged(val gender: String) : UpdateVictimFormEvent
    data class ContactChanged(val contact: String) : UpdateVictimFormEvent
    data class DescriptionChanged(val description: String) : UpdateVictimFormEvent
    data class StatusChanged(val status: String) : UpdateVictimFormEvent
    data class IsEvacuatedChanged(val isEvacuated: Boolean) : UpdateVictimFormEvent
}