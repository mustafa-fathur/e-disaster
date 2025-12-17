package com.example.e_disaster.ui.features.disaster.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.repository.DisasterRepository
import com.example.e_disaster.data.repository.DisasterVictimRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisasterDetailUiState(
    val disaster: Disaster? = null,
    val victims: List<DisasterVictim> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAssigned: Boolean = false,
    val joinStatusMessage: String? = null,
)

@HiltViewModel
class DisasterDetailViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    private val victimRepository: DisasterVictimRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DisasterDetailUiState())
    val uiState: StateFlow<DisasterDetailUiState> = _uiState.asStateFlow()

    private var currentDisasterId: String? = null

    fun getDisasterDetails(disasterId: String) {
        if (disasterId == currentDisasterId && !_uiState.value.isLoading) return
        currentDisasterId = disasterId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val disasterDetailsDeferred = async { disasterRepository.getDisasterById(disasterId) }
                val assignmentStatusDeferred = async { disasterRepository.isUserAssigned(disasterId) }

                val disasterDetails = disasterDetailsDeferred.await()
                val assignmentStatus = assignmentStatusDeferred.await()

                val victims = if (assignmentStatus) {
                    victimRepository.getDisasterVictims(disasterId)
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        disaster = disasterDetails,
                        isAssigned = assignmentStatus,
                        victims = victims
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat detail: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun joinDisaster() {
        viewModelScope.launch {
            currentDisasterId?.let { id ->
                try {
                    _uiState.update { it.copy(joinStatusMessage = null) }
                    val message = disasterRepository.joinDisaster(id)
                    _uiState.update { it.copy(joinStatusMessage = message) }
                    getDisasterDetails(id)
                } catch (e: Exception) {
                    _uiState.update { it.copy(joinStatusMessage = "Gagal bergabung: ${e.message}") }
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearJoinStatusMessage() {
        _uiState.update { it.copy(joinStatusMessage = null) }
    }

    fun refreshVictims() {
        viewModelScope.launch {
            currentDisasterId?.let { id ->
                _uiState.update { it.copy(isLoading = true) }
                try {
                    val victims = victimRepository.getDisasterVictims(id)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            victims = victims
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal refresh data korban.") }
                }
            }
        }
    }
}
