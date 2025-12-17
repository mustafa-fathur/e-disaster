package com.example.e_disaster.ui.features.disaster.detail

import android.util.Log
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
                // Get disaster details - this might throw if not in local DB and offline
                val disasterDetails = try {
                    disasterRepository.getDisasterById(disasterId)
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Bencana tidak ditemukan di database lokal. Harap sambungkan ke internet untuk memuat detail bencana."
                        )
                    }
                    return@launch
                }
                
                // Check assignment status - handle gracefully if fails
                val assignmentStatus = try {
                    disasterRepository.isUserAssigned(disasterId)
                } catch (e: Exception) {
                    Log.w("DisasterDetailVM", "Failed to check assignment status: ${e.message}")
                    // If offline and check fails, try to load victims anyway (might have local data)
                    // This allows viewing victims that were previously loaded
                    false // Default to not assigned for UI (hides FAB), but still try to load victims
                }

                // Get victims - try to load from local DB even if assignment status unknown
                // This allows offline access to previously loaded victims
                val victims = try {
                    victimRepository.getDisasterVictims(disasterId)
                } catch (e: Exception) {
                    Log.w("DisasterDetailVM", "Failed to load victims: ${e.message}")
                    emptyList() // Return empty list if fails
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
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    // Force refresh from API if online
                    try {
                        victimRepository.refreshDisasterVictims(id)
                        Log.d("DisasterDetailVM", "Force refreshed victims from API for disaster: $id")
                    } catch (e: Exception) {
                        Log.w("DisasterDetailVM", "Failed to force refresh from API, using local data: ${e.message}")
                    }
                    // Get from repository (will return local data, which is now updated)
                    val victims = victimRepository.getDisasterVictims(id)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            victims = victims,
                            errorMessage = null
                        )
                    }
                    Log.d("DisasterDetailVM", "Refreshed ${victims.size} victims for disaster: $id")
                } catch (e: Exception) {
                    Log.e("DisasterDetailVM", "Failed to refresh victims: ${e.message}", e)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Gagal refresh data korban: ${e.message}"
                        ) 
                    }
                }
            }
        }
    }
}
