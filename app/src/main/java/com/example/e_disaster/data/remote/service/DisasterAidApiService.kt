package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster_aid.CreateAidResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidDetailResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidListResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.UpdateAidRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface DisasterAidApiService {

    @GET("disasters/{disasterId}/aids")
    suspend fun getDisasterAids(
        @Path("disasterId") disasterId: String,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): DisasterAidListResponse

    @GET("disasters/{disasterId}/aids/{aidId}")
    suspend fun getDisasterAidById(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String
    ): DisasterAidDetailResponse

    @Multipart
    @POST
    suspend fun createAid(
        @Url url: String,
        @Part("title") title: RequestBody,
        @Part("category") category: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("unit") unit: RequestBody,
        @Part("description") description: RequestBody,
        @Part("donator") donator: RequestBody,
        @Part("location") location: RequestBody,
        @Part images: List<MultipartBody.Part>?
    ): CreateAidResponse

    @PUT("disasters/{disasterId}/aids/{aidId}")
    suspend fun updateAid(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String,
        @Body request: UpdateAidRequest
    ): CreateAidResponse

    @DELETE("disasters/{disasterId}/aids/{aidId}")
    suspend fun deleteAid(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String
    ): Response<Unit>
}