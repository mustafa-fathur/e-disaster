package com.example.e_disaster.ui.features.disaster_victim.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.repository.DisasterVictimRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisasterVictimDetailUiState(
    val victim: DisasterVictim? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false
)

@HiltViewModel
class DisasterVictimDetailViewModel @Inject constructor(
    private val repository: DisasterVictimRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DisasterVictimDetailUiState())
    val uiState: StateFlow<DisasterVictimDetailUiState> = _uiState.asStateFlow()

    fun loadVictimDetail(disasterId: String, victimId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val victim = repository.getDisasterVictimDetail(disasterId, victimId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        victim = victim
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat detail korban: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun deleteVictim(disasterId: String, victimId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            try {
                repository.deleteDisasterVictim(disasterId, victimId)
                _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = "Gagal menghapus data: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }
}