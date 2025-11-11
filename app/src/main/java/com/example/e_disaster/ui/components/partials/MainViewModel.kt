package com.example.e_disaster.ui.components.partials

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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // This will hold the user data that can be observed by any screen.
    var user by mutableStateOf<User?>(null)
        private set

    init {
        // Fetch the user profile as soon as this ViewModel is created.
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                // We re-use the same repository function from ProfileViewModel
                user = authRepository.getProfile()
            } catch (e: Exception) {
                // Handle error if needed, e.g., by setting a default user or error state
                user = null
            }
        }
    }
}