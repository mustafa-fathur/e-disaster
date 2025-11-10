package com.example.e_disaster.data.repository

import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.service.AuthApiService
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

// Hilt knows this should be a singleton because we defined it in AppModule
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences
) {
    /**
     * Performs the login operation.
     * 1. Calls the API with the login request.
     * 2. If successful, saves the received token to DataStore.
     */
    suspend fun login(request: LoginRequest) {
        val response = apiService.login(request)
        // The API response includes the token, which we save
        userPreferences.saveAuthToken(response.token)
    }

    suspend fun checkHealth(): ResponseBody {
        return apiService.healthCheck()
    }

    /**
     * Clears the authentication token from local storage.
     */
    suspend fun logout() {
        userPreferences.clearAuthToken()
    }

    // Add other functions like logout later
    // suspend fun logout() { ... }
}
    