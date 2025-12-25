package com.example.e_disaster.ui.features.disaster_aid.detail

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.data.repository.DisasterAidRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DisasterAidDetailUiState {
    object Idle : DisasterAidDetailUiState()
    object Loading : DisasterAidDetailUiState()
    data class Success(val aid: DisasterAid) : DisasterAidDetailUiState()
    data class Error(val message: String) : DisasterAidDetailUiState()
}

@HiltViewModel
class DisasterAidDetailViewModel @Inject constructor(
    private val repository: DisasterAidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DisasterAidDetailUiState>(DisasterAidDetailUiState.Idle)
    val uiState: StateFlow<DisasterAidDetailUiState> = _uiState.asStateFlow()

    fun loadAidDetail(disasterId: String, aidId: String) {
        viewModelScope.launch {
            _uiState.value = DisasterAidDetailUiState.Loading
            try {
                val response = repository.getDisasterAidById(disasterId, aidId)
                val aidDto = response.data
                
                if (aidDto != null) {
                    val aid = repository.mapDisasterAidDtoToModel(aidDto)
                    _uiState.value = DisasterAidDetailUiState.Success(aid)
                } else {
                    _uiState.value = DisasterAidDetailUiState.Error("Data bantuan tidak ditemukan")
                }
            } catch (e: Exception) {
                _uiState.value = DisasterAidDetailUiState.Error(
                    e.message ?: "Gagal memuat detail bantuan"
                )
            }
        }
    }

    fun addPicture(disasterId: String, aidId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                repository.addAidPicture(aidId, imageUri, context)
                loadAidDetail(disasterId, aidId)
            } catch (e: Exception) {
                _uiState.update { 
                    DisasterAidDetailUiState.Error("Gagal mengunggah gambar: ${e.message}") 
                }
                e.printStackTrace()
            }
        }
    }

    fun deletePicture(disasterId: String, aidId: String, pictureId: String) {
        viewModelScope.launch {
            try {
                repository.deleteAidPicture(aidId, pictureId)
                loadAidDetail(disasterId, aidId)
            } catch (e: Exception) {
                _uiState.update { 
                    DisasterAidDetailUiState.Error("Gagal menghapus gambar: ${e.message}") 
                }
                e.printStackTrace()
            }
        }
    }
}

