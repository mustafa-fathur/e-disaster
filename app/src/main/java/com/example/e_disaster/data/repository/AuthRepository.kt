package com.example.e_disaster.data.repository

import android.content.Context
import android.util.Log
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.local.database.dao.UserDao
import com.example.e_disaster.data.mapper.UserMapper
import com.example.e_disaster.data.model.User
import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.LogoutRequest
import com.example.e_disaster.data.remote.dto.auth.RegisterRequest
import com.example.e_disaster.data.remote.dto.auth.RegisterResponse
import com.example.e_disaster.data.remote.dto.auth.UserDto
import com.example.e_disaster.data.remote.service.AuthApiService
import com.example.e_disaster.utils.DeviceUtils
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences,
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) {
    suspend fun checkHealth(): ResponseBody {
        return apiService.healthCheck()
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return apiService.register(request)
    }

    suspend fun login(request: LoginRequest) {
        val fcmToken = getFcmToken()
        val deviceId = DeviceUtils.getDeviceId(context)
        val deviceName = DeviceUtils.getDeviceName()
        val appVersion = DeviceUtils.getAppVersion()

        val fullRequest = request.copy(
            fcmToken = fcmToken,
            deviceId = deviceId,
            deviceName = deviceName,
            appVersion = appVersion
        )

        Log.d("AuthRepo", "Login request: $fullRequest")

        val response = apiService.login(fullRequest)
        userPreferences.saveAuthToken(response.token)
        fcmToken?.let { userPreferences.saveFcmToken(it) }
        
        // Save user data to local database
        val userEntity = with(UserMapper) { response.user.toEntity() }
        userDao.insertUser(userEntity)
        Log.d("AuthRepo", "User data saved to local database: ${userEntity.id}")
    }

    suspend fun logout() {
        val fcmToken = userPreferences.fcmToken.first()
        if (fcmToken != null) {
            try {
                val logoutRequest = LogoutRequest(fcmToken = fcmToken)
                apiService.logout(logoutRequest)
                Log.d("AuthRepo", "Successfully unregistered FCM token on server.")
            } catch (e: Exception) {
                Log.e("AuthRepo", "Failed to unregister FCM token on server.", e)
            }
        }
        userPreferences.clearAll()
        
        // Clear local user data
        try {
            val countBefore = userDao.getCurrentUser()?.let { 1 } ?: 0
            Log.d("AuthRepo", "Users in database before logout: $countBefore")
            userDao.deleteAll()
            val countAfter = userDao.getCurrentUser()?.let { 1 } ?: 0
            Log.d("AuthRepo", "Users in database after logout: $countAfter")
            if (countAfter == 0) {
                Log.d("AuthRepo", "Local user data cleared successfully")
            } else {
                Log.w("AuthRepo", "Warning: User data still exists after deleteAll()")
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Failed to clear local user data", e)
            e.printStackTrace()
        }
    }

    private suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("FCM_TOKEN", "Failed to get FCM token", e)
            null
        }
    }
    
    suspend fun getProfile(): User {
        val response = apiService.getProfile()
        
        // Save user data to local database
        val userEntity = with(UserMapper) { response.user.toEntity() }
        userDao.insertUser(userEntity)
        Log.d("AuthRepo", "User profile saved to local database: ${userEntity.id}")

        return with(UserMapper) { response.user.toModel() }
    }
    
    suspend fun getLocalUser(): User? {
        val userEntity = userDao.getCurrentUser()
        return userEntity?.let { with(UserMapper) { it.toModel() } }
    }
}
    