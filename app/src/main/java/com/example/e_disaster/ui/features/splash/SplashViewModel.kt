package com.example.e_disaster.ui.features.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.local.database.dao.UserDao
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
    private val userDao: UserDao
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState = _authState.asStateFlow()

    init {
        checkUserLoginState()
    }

    private fun checkUserLoginState() {
        viewModelScope.launch {
            val token = userPreferences.authToken.first()
            
            if (!token.isNullOrBlank()) {
                // Token exists - check if we have local user data
                val localUser = userDao.getCurrentUser()
                
                if (localUser != null) {
                    // We have local user data - allow offline access
                    Log.d("Splash", "User authenticated (offline mode with local data)")
                    _authState.value = AuthState.AUTHENTICATED
                } else {
                    // Token exists but no local user data - still allow (will fetch profile later)
                    Log.d("Splash", "Token exists but no local user data")
                    _authState.value = AuthState.AUTHENTICATED
                }
            } else {
                _authState.value = AuthState.UNAUTHENTICATED
            }
        }
    }
}