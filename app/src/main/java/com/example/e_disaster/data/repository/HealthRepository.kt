package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.HealthCheckResponse
import com.example.e_disaster.data.remote.ApiClient
import retrofit2.Response

class HealthRepository {
    suspend fun getHealthStatus(): Response<HealthCheckResponse> {
        return ApiClient.disasterAidApi.getHealth()
    }
}