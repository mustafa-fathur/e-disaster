package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.dashboard.DashboardResponse
import com.example.e_disaster.data.remote.dto.disaster.CreateDisasterRequest
import com.example.e_disaster.data.remote.dto.disaster.CreateDisasterResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterDetailResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterListResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerCheckResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerListResponse
import com.example.e_disaster.data.remote.dto.disaster.UpdateDisasterRequest
import com.example.e_disaster.data.remote.dto.disaster.UpdateDisasterResponse
import com.example.e_disaster.data.remote.dto.general.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DisasterApiService {

    @GET("dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("disasters")
    suspend fun getDisasters(
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("type") type: String? = null
    ): DisasterListResponse

    @GET("disasters/{disasterId}")
    suspend fun getDisasterById(
        @Path("disasterId") disasterId: String
    ): DisasterDetailResponse

    @POST("disasters")
    suspend fun createDisaster(
        @Body request: CreateDisasterRequest
    ): CreateDisasterResponse

    @PUT("disasters/{disasterId}")
    suspend fun updateDisaster(
        @Path("disasterId") disasterId: String,
        @Body request: UpdateDisasterRequest
    ): UpdateDisasterResponse

    @POST("disasters/{disasterId}/volunteers")
    suspend fun joinDisaster(
        @Path("disasterId") disasterId: String
    ): MessageResponse

    @GET("disasters/{disasterId}/volunteer-check")
    suspend fun checkAssignment(
        @Path("disasterId") disasterId: String
    ): DisasterVolunteerCheckResponse

    @GET("disasters/{disasterId}/volunteers")
    suspend fun getDisasterVolunteers(
        @Path("disasterId") disasterId: String
    ): DisasterVolunteerListResponse
}

