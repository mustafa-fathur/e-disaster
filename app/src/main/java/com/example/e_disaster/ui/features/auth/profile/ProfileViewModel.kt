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
import javax.inject.Inject
import java.io.IOException
import retrofit2.HttpException

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
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                user = authRepository.getProfile()
            } catch (e: HttpException) {
                errorMessage = "API Error: ${e.code()} - ${e.message()}"
            } catch (e: IOException) {
                errorMessage = "Network Error: ${e.message}"
            } catch (e: Exception) {
                errorMessage = "An Error Occurred: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
