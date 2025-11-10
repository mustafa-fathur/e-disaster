package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.ResponseBody

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Add this new function for the health check
    @GET("health")
    suspend fun healthCheck(): ResponseBody
}

    