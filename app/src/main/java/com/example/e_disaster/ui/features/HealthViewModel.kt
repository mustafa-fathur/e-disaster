package com.example.e_disaster.ui.features

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.HealthCheckResponse
import com.example.e_disaster.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    private val healthRepository = HealthRepository()

    private val _healthStatus = MutableStateFlow<HealthCheckResponse?>(null)
    val healthStatus: StateFlow<HealthCheckResponse?> = _healthStatus

    fun checkHealth() {
        viewModelScope.launch {
            try {
                val response = healthRepository.getHealthStatus()
                if (response.isSuccessful) {
                    _healthStatus.value = response.body()
                } else {
                    Log.e("HealthViewModel", "API call failed with response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HealthViewModel", "API call failed with exception", e)
            }
        }
    }
}