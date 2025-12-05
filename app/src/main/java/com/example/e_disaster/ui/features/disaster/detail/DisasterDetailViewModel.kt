package com.example.e_disaster.ui.features.disaster.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisasterDetailUiState(
    val isLoading: Boolean = true,
    val disaster: Disaster? = null,
    val isAssigned: Boolean = false,
    val errorMessage: String? = null,
    val joinStatusMessage: String? = null
)

@HiltViewModel
class DisasterDetailViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(DisasterDetailUiState())
        private set

    private val disasterId: String = checkNotNull(savedStateHandle["disasterId"])

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // Panggilan ini sekarang akan bekerja sesuai dengan arsitektur backend Anda
                val disasterDetail = disasterRepository.getDisasterById(disasterId)
                val assignmentStatus = disasterRepository.checkAssignmentStatus(disasterId)

                uiState = uiState.copy(
                    isLoading = false,
                    disaster = disasterDetail,
                    isAssigned = assignmentStatus
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat data detail: ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }

    fun joinDisaster() {
        viewModelScope.launch {
            try {
                // Panggilan ini sekarang akan mengarah ke endpoint yang benar
                disasterRepository.joinDisaster(disasterId)
                uiState = uiState.copy(
                    isAssigned = true,
                    joinStatusMessage = "Anda berhasil bergabung!"
                )
            } catch (e: Exception) {
                uiState = uiState.copy(joinStatusMessage = "Gagal bergabung: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun clearJoinStatusMessage() {
        uiState = uiState.copy(joinStatusMessage = null)
    }
}
