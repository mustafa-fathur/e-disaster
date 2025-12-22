package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.DisasterReport
import com.example.e_disaster.data.repository.DisasterReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisasterReportDetailUiState(
    val isLoading: Boolean = true,
    val report: DisasterReport? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DisasterReportDetailViewModel @Inject constructor(
    private val disasterReportRepository: DisasterReportRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(DisasterReportDetailUiState())
        private set

    private val disasterId: String? = savedStateHandle["disasterId"]
    private val reportId: String? = savedStateHandle["reportId"]

    init {
        loadReport()
    }

    fun loadReport() {
        val dId = disasterId
        val rId = reportId
        if (dId == null || rId == null) {
            uiState = uiState.copy(isLoading = false, errorMessage = "ID laporan atau bencana tidak tersedia")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val report = disasterReportRepository.getDisasterReport(dId, rId)
                uiState = uiState.copy(isLoading = false, report = report)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gagal memuat laporan: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

