package com.example.e_disaster.ui.features.disaster_aid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.disaster_aid.CreateAidRequest
import com.example.e_disaster.data.remote.service.DisasterAidApiService
import com.example.e_disaster.data.remote.service.DisasterApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException

sealed class AddAidUiState {
    object Idle : AddAidUiState()
    object Loading : AddAidUiState()
    object Success : AddAidUiState()
    data class Error(val message: String) : AddAidUiState()
}

@HiltViewModel
class AddDisasterAidViewModel @Inject constructor(
    private val disasterAidApiService: DisasterAidApiService,
    private val disasterApiService: DisasterApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAidUiState>(AddAidUiState.Idle)
    val uiState: StateFlow<AddAidUiState> = _uiState.asStateFlow()

    private val _lat = MutableStateFlow<Double?>(null)
    val lat: StateFlow<Double?> = _lat.asStateFlow()

    private val _long = MutableStateFlow<Double?>(null)
    val long: StateFlow<Double?> = _long.asStateFlow()

    private val _locationLoading = MutableStateFlow(false)
    val locationLoading: StateFlow<Boolean> = _locationLoading.asStateFlow()

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()

    fun loadDisasterLocation(disasterId: String) {
        viewModelScope.launch {
            _locationLoading.value = true
            _locationError.value = null
            try {
                val disaster = disasterApiService.getDisaster(disasterId)
                _lat.value = disaster.lat
                _long.value = disaster.long
            } catch (e: HttpException) {
                _locationError.value = "API Error: ${e.code()} - ${e.message()}"
            } catch (e: IOException) {
                _locationError.value = "Network Error: ${e.message}"
            } catch (e: Exception) {
                _locationError.value = e.message ?: "Unexpected error"
            } finally {
                _locationLoading.value = false
            }
        }
    }

    fun submit(
        disasterId: String,
        name: String,
        category: String,
        quantity: Int,
        description: String,
        location: String
    ) {
        if (_uiState.value == AddAidUiState.Loading) return

        val latValue = _lat.value
        val longValue = _long.value

        viewModelScope.launch {
            _uiState.value = AddAidUiState.Loading
            try {
                val request = CreateAidRequest(
                    name = name,
                    category = category,
                    quantity = quantity,
                    description = description,
                    location = location,
                    lat = latValue,
                    long = longValue
                )
                disasterAidApiService.createAid(disasterId, request)
                _uiState.value = AddAidUiState.Success
            } catch (e: HttpException) {
                _uiState.value = AddAidUiState.Error("API Error: ${e.code()} - ${e.message()}")
            } catch (e: IOException) {
                _uiState.value = AddAidUiState.Error("Network Error: ${e.message}")
            } catch (e: Exception) {
                _uiState.value = AddAidUiState.Error(e.message ?: "Unexpected error")
            }
        }
    }
}