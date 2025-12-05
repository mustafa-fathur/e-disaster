package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterListResponse
import com.example.e_disaster.data.remote.dto.general.MessageResponse
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportListResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DisasterApiService {

    @GET("disasters")
    suspend fun getDisasters(): DisasterListResponse

    @GET("disasters/{disasterId}")
    suspend fun getDisasterById(
        @Path("disasterId") disasterId: String
    ): DisasterDto

    @POST("disasters/{disasterId}/volunteers")
    suspend fun joinDisaster(
        @Path("disasterId") disasterId: String
    ): MessageResponse

    @GET("disasters/{disasterId}/reports")
    suspend fun getDisasterReports(
        @Path("disasterId") disasterId: String
    ): DisasterReportListResponse
}
