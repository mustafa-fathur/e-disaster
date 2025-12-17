package com.example.e_disaster.ui.features.auth.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.User
import com.example.e_disaster.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val profileResult = authRepository.getProfile()

            profileResult.onSuccess { fetchedUser ->
                user = fetchedUser
                isLoading = false
            }.onFailure { exception ->
                errorMessage = when (exception) {
                    is HttpException -> "API Error: ${exception.code()} - Unauthorized"
                    else -> "An unexpected error occurred: ${exception.message}"
                }
                user = null
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            user = null
        }
    }
}
