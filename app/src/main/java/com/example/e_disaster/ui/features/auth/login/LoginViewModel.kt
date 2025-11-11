package com.example.e_disaster.ui.features.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define states for the UI to observe
sealed class LoginUiState {
    object Idle : LoginUiState() // Before anything happens
    object Loading : LoginUiState() // When the login process is running
    object Success : LoginUiState() // On successful login
    data class Error(val message: String) : LoginUiState() // On failure
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Private mutable state flow for internal use
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    // Public immutable state flow for the UI to collect
    val uiState = _uiState.asStateFlow()

    // States for the email and password text fields
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    // --- NEW: State for the health check message ---
    var healthCheckMessage by mutableStateOf("Checking API status...")
        private set

    // --- NEW: init block to run the health check automatically ---
    init {
        healthCheck()
    }

    // Functions to be called by the UI when the text fields change
    fun onEmailChange(newValue: String) {
        email = newValue
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
    }

    fun loginUser() {
        // Don't start another login if one is already in progress
        if (_uiState.value == LoginUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val request = LoginRequest(email, password)
                authRepository.login(request)
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                // In a real app, parse the specific HTTP error
                _uiState.value = LoginUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    private fun healthCheck() { // <-- Changed to private
        viewModelScope.launch {
            try {
                // We call the repository and get the raw response
                val response = authRepository.checkHealth()
                // --- NEW: Update the dedicated state variable ---
                healthCheckMessage = "API Status: ${response.string()}"
            } catch (e: Exception) {
                // If it fails, we show the actual exception message
                healthCheckMessage = "API Error: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = LoginUiState.Error("Token Cleared!") // Use Error state to show a toast
        }
    }
}
