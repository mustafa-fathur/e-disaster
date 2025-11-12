package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster_aid.CreateAidRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface DisasterAidApiService {

    @POST("disasters/{disasterId}/aids")
    suspend fun createAid(
        @Path("disasterId") disasterId: String,
        @Body request: CreateAidRequest
    ): ResponseBody
}