package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.disaster_report.CreateDisasterReportRequest
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddDisasterReportUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddDisasterReportViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddDisasterReportUiState())
        private set

    fun createReport(disasterId: String?, title: String, description: String, isFinalStage: Boolean) {
        if (disasterId.isNullOrBlank()) {
            uiState = uiState.copy(errorMessage = "ID bencana tidak tersedia")
            return
        }
        val safeId = disasterId.trim()
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, success = false)
            try {
                val request = CreateDisasterReportRequest(
                    title = title,
                    description = description,
                    isFinalStage = isFinalStage
                )
                disasterRepository.createDisasterReport(safeId, request)
                uiState = uiState.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gagal menyimpan laporan: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
