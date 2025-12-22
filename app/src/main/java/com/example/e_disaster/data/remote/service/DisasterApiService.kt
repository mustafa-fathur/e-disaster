package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterDetailResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterListResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerCheckResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerListResponse
import com.example.e_disaster.data.remote.dto.general.MessageResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DisasterApiService {

    @GET("disasters")
    suspend fun getDisasters(): DisasterListResponse

    @GET("disasters/{disasterId}")
    suspend fun getDisasterById(
        @Path("disasterId") disasterId: String
    ): DisasterDetailResponse

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
