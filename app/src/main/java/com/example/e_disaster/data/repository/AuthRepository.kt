package com.example.e_disaster.data.repository

import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.model.User
import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.RegisterRequest
import com.example.e_disaster.data.remote.dto.auth.RegisterResponse
import com.example.e_disaster.data.remote.dto.auth.UserDto
import com.example.e_disaster.data.remote.service.AuthApiService
import okhttp3.ResponseBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun checkHealth(): ResponseBody {
        return apiService.healthCheck()
    }

    suspend fun register(request: RegisterRequest): RegisterResponse {
        return apiService.register(request)
    }

    suspend fun login(request: LoginRequest) {
        val response = apiService.login(request)
        userPreferences.saveAuthToken(response.token)
    }

    suspend fun logout() {
        userPreferences.clearAuthToken()
    }
    
    suspend fun getProfile(): User {
        val response = apiService.getProfile()

        return mapUserDtoToUser(response.user)
    }

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
                "https://e-disaster.fathur.tech" + dto.profilePicture.url
            } else {
                "Tidak ada data"
            }
        )
    }
}
    