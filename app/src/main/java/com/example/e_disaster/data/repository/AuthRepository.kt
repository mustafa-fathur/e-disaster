package com.example.e_disaster.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.model.User
import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.UserDto
import com.example.e_disaster.data.remote.service.AuthApiService
import okhttp3.ResponseBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// Hilt knows this should be a singleton because we defined it in AppModule
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun checkHealth(): ResponseBody {
        return apiService.healthCheck()
    }
    
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

    /**
     * Clears the authentication token from local storage.
     */
    suspend fun logout() {
        userPreferences.clearAuthToken()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getProfile(): User {
        val response = apiService.getProfile()

        return mapUserDtoToUser(response.user)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mapUserDtoToUser(dto: UserDto): User {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))

        val formattedDate = try {
            val date = LocalDate.parse(dto.dateOfBirth, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            "Tidak ada data"
        }
        return User(
            id = dto.id,
            name = dto.name,
            email = dto.email,
            userType = dto.type,
            status = dto.status,
            nik = dto.nik ?: "Tidak ada data",
            phone = dto.phone ?: "Tidak ada data",
            address = dto.address ?: "Tidak ada data",
            dateOfBirth = formattedDate,
            gender = if (dto.gender == false) "Laki-laki" else "Perempuan",
            profilePicture = if (dto.profilePicture != null) {
                "http://10.0.2.2:8000" + dto.profilePicture.url
            } else {
                "Tidak ada data"
            }
        )
    }
}
    