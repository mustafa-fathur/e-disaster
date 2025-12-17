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

    var user by mutableStateOf<User?>(null)
        private set

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            val profileResult = authRepository.getProfile()

            profileResult.onSuccess { fetchedUser ->
                user = fetchedUser
            }.onFailure {
                user = null
            }
        }
    }
}