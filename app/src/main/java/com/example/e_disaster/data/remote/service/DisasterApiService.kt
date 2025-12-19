package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterListResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerCheckResponse
import com.example.e_disaster.data.remote.dto.disaster.DisasterVolunteerListResponse
import com.example.e_disaster.data.remote.dto.general.MessageResponse
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportListResponse
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportDetailResponse
import com.example.e_disaster.data.remote.dto.disaster_report.CreateDisasterReportRequest
import com.example.e_disaster.data.remote.dto.disaster_report.UpdateDisasterReportRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("disasters/{disasterId}/volunteer-check")
    suspend fun checkAssignment(
        @Path("disasterId") disasterId: String
    ): DisasterVolunteerCheckResponse

    @GET("disasters/{disasterId}/volunteers")
    suspend fun getDisasterVolunteers(
        @Path("disasterId") disasterId: String
    ): DisasterVolunteerListResponse

    @GET("disasters/{disasterId}/reports")
    suspend fun getDisasterReports(
        @Path("disasterId") disasterId: String
    ): DisasterReportListResponse

    @GET("disasters/{disasterId}/reports/{reportId}")
    suspend fun getDisasterReport(
        @Path("disasterId") disasterId: String,
        @Path("reportId") reportId: String
    ): DisasterReportDetailResponse

    @POST("disasters/{disasterId}/reports")
    suspend fun createDisasterReport(
        @Path("disasterId") disasterId: String,
        @Body request: CreateDisasterReportRequest
    ): DisasterReportDetailResponse

    @PUT("disasters/{disasterId}/reports/{reportId}")
    suspend fun updateDisasterReport(
        @Path("disasterId") disasterId: String,
        @Path("reportId") reportId: String,
        @Body request: UpdateDisasterReportRequest
    ): DisasterReportDetailResponse
}
