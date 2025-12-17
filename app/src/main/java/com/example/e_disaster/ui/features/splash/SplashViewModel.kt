package com.example.e_disaster.ui.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthState {
    LOADING,
    AUTHENTICATED,
    UNAUTHENTICATED
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState = _authState.asStateFlow()

    init {
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        viewModelScope.launch {
            val token = userPreferences.authToken.first()

            if (token.isNullOrBlank()) {
                _authState.value = AuthState.UNAUTHENTICATED
            } else {
                val profileResult = authRepository.getProfile()

                profileResult.onSuccess {
                    _authState.value = AuthState.AUTHENTICATED
                }.onFailure {
                    userPreferences.clearAll()
                    _authState.value = AuthState.UNAUTHENTICATED
                }
            }
        }
    }
}