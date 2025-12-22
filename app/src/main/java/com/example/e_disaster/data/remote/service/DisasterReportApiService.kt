package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster_report.CreateDisasterReportRequest
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportDetailResponse
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportListResponse
import com.example.e_disaster.data.remote.dto.disaster_report.UpdateDisasterReportRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface DisasterReportApiService {
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

    @Multipart
    @POST("pictures/disaster_report/{reportId}")
    suspend fun uploadReportImages(
        @Path("reportId") reportId: String,
        @Part images: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @PUT("disasters/{disasterId}/reports/{reportId}")
    suspend fun updateDisasterReport(
        @Path("disasterId") disasterId: String,
        @Path("reportId") reportId: String,
        @Body request: UpdateDisasterReportRequest
    ): DisasterReportDetailResponse
}
