package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.LoginResponse
import com.example.e_disaster.data.remote.dto.auth.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.ResponseBody

interface AuthApiService {

    @GET("health")
    suspend fun healthCheck(): ResponseBody

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("me")
    suspend fun getProfile(): ProfileResponse
}

    