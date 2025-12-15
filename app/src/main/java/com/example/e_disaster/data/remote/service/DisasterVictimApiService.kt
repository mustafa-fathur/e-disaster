package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster_victim.AddVictimResponse
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDetailResponse
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface DisasterVictimApiService {

    @GET("disasters/{disasterId}/victims")
    suspend fun getDisasterVictims(
        @Path("disasterId") disasterId: String
    ): DisasterVictimListResponse

    @GET("disasters/{disasterId}/victims/{victimId}")
    suspend fun getDisasterVictimDetail(
        @Path("disasterId") disasterId: String,
        @Path("victimId") victimId: String
    ): DisasterVictimDetailResponse

    @Multipart
    @POST
    suspend fun addDisasterVictim(
        @Url url: String,
        @Part("nik") nik: RequestBody,
        @Part("name") name: RequestBody,
        @Part("date_of_birth") dateOfBirth: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("status") status: RequestBody,
        @Part("is_evacuated") isEvacuated: RequestBody,
        @Part("contact_info") contactInfo: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part images: List<MultipartBody.Part>?
    ): AddVictimResponse
}