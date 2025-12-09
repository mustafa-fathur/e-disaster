package com.example.e_disaster.ui.features.disaster.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.DisasterReport
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisasterReportsUiState(
    val isLoading: Boolean = true,
    val reports: List<DisasterReport> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class DisasterReportsViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(DisasterReportsUiState())
        private set

    private val disasterId: String? = savedStateHandle["disasterId"]

    init {
        loadReports()
    }

    fun loadReports() {
        val id = disasterId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val reports = disasterRepository.getDisasterReports(id)
                uiState = uiState.copy(isLoading = false, reports = reports)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gagal memuat laporan: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

