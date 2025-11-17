package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DisasterApiService {

    @GET("disasters")
    suspend fun getDisasters(): DisasterListResponse

    @GET("disasters/{disasterId}")
    suspend fun getDisaster(
        @Path("disasterId") disasterId: String
    ): DisasterDto
}