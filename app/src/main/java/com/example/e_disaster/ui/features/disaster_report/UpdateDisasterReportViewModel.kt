package com.example.e_disaster.ui.features.disaster_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.repository.DisasterReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateReportUiState(
    val title: String = "",
    val description: String = "",
    val isFinalStage: Boolean = false,
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class UpdateDisasterReportViewModel @Inject constructor(
    private val disasterReportRepository: DisasterReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateReportUiState())
    val uiState: StateFlow<UpdateReportUiState> = _uiState.asStateFlow()

    fun loadInitialData(disasterId: String, reportId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val report = disasterReportRepository.getDisasterReport(disasterId, reportId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        title = report.title,
                        description = report.description,
                        isFinalStage = report.isFinalStage
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data laporan: ${e.message}"
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onIsFinalChange(isFinal: Boolean) {
        _uiState.update { it.copy(isFinalStage = isFinal) }
    }

    fun submitUpdate(disasterId: String, reportId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            try {
                disasterReportRepository.updateDisasterReport(
                    disasterId = disasterId,
                    reportId = reportId,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    isFinalStage = _uiState.value.isFinalStage
                )
                _uiState.update { it.copy(isUpdating = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        errorMessage = "Gagal memperbarui laporan: ${e.message}"
                    )
                }
            }
        }
    }
}