package com.example.e_disaster.ui.features.disaster_aid.update

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

data class UpdateAidFormState(
    val title: String = "",
    val category: String = "food",
    val quantity: String = "",
    val unit: String = "",
    val description: String = "",
    val donator: String = "",
    val location: String = "",

    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationError: String? = null
)

sealed class UpdateAidUiState {
    object Idle : UpdateAidUiState()
    object Loading : UpdateAidUiState()
    object Success : UpdateAidUiState()
    data class Error(val message: String) : UpdateAidUiState()
}

@HiltViewModel
class UpdateDisasterAidViewModel @Inject constructor(
    private val repository: DisasterAidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateAidUiState>(UpdateAidUiState.Idle)
    val uiState: StateFlow<UpdateAidUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(UpdateAidFormState())
    val formState: StateFlow<UpdateAidFormState> = _formState.asStateFlow()

    fun loadInitialData(disasterId: String, aidId: String) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getDisasterAidById(disasterId, aidId)
                val aidDto = response.data

                if (aidDto != null) {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            title = aidDto.title ?: "",
                            category = aidDto.category ?: "food",
                            quantity = aidDto.quantity?.toString() ?: "",
                            unit = aidDto.unit ?: "",
                            description = aidDto.description ?: "",
                            donator = aidDto.donator ?: "",
                            location = aidDto.location ?: ""
                        )
                    }
                } else {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Data bantuan tidak ditemukan"
                        )
                    }
                }
            } catch (e: Exception) {
                _formState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat data bantuan: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun onEvent(event: UpdateAidFormEvent) {
        when (event) {
            is UpdateAidFormEvent.TitleChanged ->
                _formState.update { it.copy(title = event.title) }
            is UpdateAidFormEvent.CategoryChanged ->
                _formState.update { it.copy(category = event.category) }
            is UpdateAidFormEvent.QuantityChanged ->
                _formState.update { it.copy(quantity = event.quantity) }
            is UpdateAidFormEvent.UnitChanged ->
                _formState.update { it.copy(unit = event.unit) }
            is UpdateAidFormEvent.DescriptionChanged ->
                _formState.update { it.copy(description = event.description) }
            is UpdateAidFormEvent.DonatorChanged ->
                _formState.update { it.copy(donator = event.donator) }
            is UpdateAidFormEvent.LocationChanged ->
                _formState.update { it.copy(location = event.location) }
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

    fun submitUpdate(disasterId: String, aidId: String) {
        if (!validateForm()) return

        val currentState = _formState.value

        viewModelScope.launch {
            _uiState.value = UpdateAidUiState.Loading
            _formState.update { it.copy(isUpdating = true, errorMessage = null) }

            try {
                repository.updateDisasterAid(
                    disasterId = disasterId,
                    aidId = aidId,
                    title = currentState.title,
                    category = currentState.category,
                    quantity = currentState.quantity.toIntOrNull() ?: 0,
                    unit = currentState.unit,
                    description = currentState.description,
                    donator = currentState.donator,
                    location = currentState.location
                )
                _uiState.value = UpdateAidUiState.Success
                _formState.update { it.copy(isUpdating = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.value = UpdateAidUiState.Error(e.message ?: "Gagal update bantuan")
                _formState.update {
                    it.copy(
                        isUpdating = false,
                        errorMessage = "Gagal memperbarui data: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }
}

sealed interface UpdateAidFormEvent {
    data class TitleChanged(val title: String) : UpdateAidFormEvent
    data class CategoryChanged(val category: String) : UpdateAidFormEvent
    data class QuantityChanged(val quantity: String) : UpdateAidFormEvent
    data class UnitChanged(val unit: String) : UpdateAidFormEvent
    data class DescriptionChanged(val description: String) : UpdateAidFormEvent
    data class DonatorChanged(val donator: String) : UpdateAidFormEvent
    data class LocationChanged(val location: String) : UpdateAidFormEvent
}
