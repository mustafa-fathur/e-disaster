package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.auth.LoginRequest
import com.example.e_disaster.data.remote.dto.auth.LoginResponse
import com.example.e_disaster.data.remote.dto.auth.LogoutRequest
import com.example.e_disaster.data.remote.dto.auth.ProfileResponse
import com.example.e_disaster.data.remote.dto.auth.RegisterRequest
import com.example.e_disaster.data.remote.dto.auth.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import okhttp3.ResponseBody

interface AuthApiService {

    @GET("health")
    suspend fun healthCheck(): ResponseBody

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): ResponseBody

    @GET("me")
    suspend fun getProfile(): ProfileResponse
}

    