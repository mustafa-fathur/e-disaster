package com.example.e_disaster.ui.features.disaster_aid.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.repository.DisasterAidRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddAidFormState(
    val title: String = "",
    val category: String = "food",
    val quantity: String = "",
    val unit: String = "",
    val description: String = "",
    val donator: String = "",
    val location: String = "",
    val images: List<Uri> = emptyList(),
    
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationError: String? = null
)

sealed class AddAidUiState {
    object Idle : AddAidUiState()
    object Loading : AddAidUiState()
    object Success : AddAidUiState()
    data class Error(val message: String) : AddAidUiState()
}

@HiltViewModel
class AddDisasterAidViewModel @Inject constructor(
    private val repository: DisasterAidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAidUiState>(AddAidUiState.Idle)
    val uiState: StateFlow<AddAidUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AddAidFormState())
    val formState: StateFlow<AddAidFormState> = _formState.asStateFlow()

    fun onEvent(event: AddAidFormEvent) {
        when (event) {
            is AddAidFormEvent.TitleChanged -> 
                _formState.update { it.copy(title = event.title) }
            is AddAidFormEvent.CategoryChanged -> 
                _formState.update { it.copy(category = event.category) }
            is AddAidFormEvent.QuantityChanged -> 
                _formState.update { it.copy(quantity = event.quantity) }
            is AddAidFormEvent.UnitChanged -> 
                _formState.update { it.copy(unit = event.unit) }
            is AddAidFormEvent.DescriptionChanged -> 
                _formState.update { it.copy(description = event.description) }
            is AddAidFormEvent.DonatorChanged -> 
                _formState.update { it.copy(donator = event.donator) }
            is AddAidFormEvent.LocationChanged -> 
                _formState.update { it.copy(location = event.location) }
            is AddAidFormEvent.ImagesAdded -> 
                _formState.update { it.copy(images = _formState.value.images + event.uris) }
            is AddAidFormEvent.ImageRemoved -> 
                _formState.update { it.copy(images = _formState.value.images - event.uri) }
        }
    }

    private fun validateForm(): Boolean {
        val state = _formState.value
        if (state.title.isBlank() || state.quantity.isBlank() || state.unit.isBlank()) {
            _formState.update { 
                it.copy(validationError = "Judul, Jumlah, dan Satuan tidak boleh kosong.") 
            }
            return false
        }
        _formState.update { it.copy(validationError = null) }
        return true
    }

    fun submitForm(disasterId: String, context: Context) {
        if (!validateForm()) return

        val currentState = _formState.value
        
        viewModelScope.launch {
            _uiState.value = AddAidUiState.Loading
            _formState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                repository.createDisasterAid(
                    disasterId = disasterId,
                    title = currentState.title,
                    category = currentState.category,
                    quantity = currentState.quantity.toIntOrNull() ?: 0,
                    unit = currentState.unit,
                    description = currentState.description,
                    donator = currentState.donator,
                    location = currentState.location,
                    images = currentState.images,
                    context = context
                )
                _uiState.value = AddAidUiState.Success
                _formState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.value = AddAidUiState.Error(e.message ?: "Gagal membuat bantuan")
                _formState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Gagal menambahkan data: ${e.message}"
                    ) 
                }
                e.printStackTrace()
            }
        }
    }
}

sealed interface AddAidFormEvent {
    data class TitleChanged(val title: String) : AddAidFormEvent
    data class CategoryChanged(val category: String) : AddAidFormEvent
    data class QuantityChanged(val quantity: String) : AddAidFormEvent
    data class UnitChanged(val unit: String) : AddAidFormEvent
    data class DescriptionChanged(val description: String) : AddAidFormEvent
    data class DonatorChanged(val donator: String) : AddAidFormEvent
    data class LocationChanged(val location: String) : AddAidFormEvent
    data class ImagesAdded(val uris: List<Uri>) : AddAidFormEvent
    data class ImageRemoved(val uri: Uri) : AddAidFormEvent
}
