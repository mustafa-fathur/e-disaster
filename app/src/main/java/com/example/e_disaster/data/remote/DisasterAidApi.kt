package com.example.e_disaster.data.remote

import com.example.e_disaster.data.model.CreateDisasterAidRequest
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.data.model.HealthCheckResponse
import com.example.e_disaster.data.model.UpdateDisasterAidRequest
import retrofit2.Response
import retrofit2.http.*

interface DisasterAidApi {

    @GET("health")
    suspend fun getHealth(): Response<HealthCheckResponse>

    /**
     * Mendapatkan daftar bantuan untuk bencana tertentu
     */
    @GET("disasters/{disasterId}/aids")
    suspend fun getDisasterAids(
        @Path("disasterId") disasterId: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Double? = null, // dalam km
        @Query("status") status: String? = null,
        @Query("type") type: String? = null
    ): Response<List<DisasterAid>>

    /**
     * Mendapatkan daftar bantuan di sekitar lokasi tertentu (tidak terikat pada bencana tertentu)
     */
    @GET("aids/nearby")
    suspend fun getNearbyAids(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double = 10.0, // default 10km
        @Query("status") status: String? = null,
        @Query("type") type: String? = null
    ): Response<List<DisasterAid>>

    /**
     * Membuat bantuan baru untuk bencana tertentu
     */
    @POST("disasters/{disasterId}/aids")
    suspend fun createDisasterAid(
        @Path("disasterId") disasterId: String,
        @Body aidRequest: CreateDisasterAidRequest
    ): Response<DisasterAid>

    /**
     * Mendapatkan detail bantuan tertentu
     */
    @GET("disasters/{disasterId}/aids/{aidId}")
    suspend fun getDisasterAid(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String
    ): Response<DisasterAid>

    /**
     * Update bantuan tertentu
     */
    @PUT("disasters/{disasterId}/aids/{aidId}")
    suspend fun updateDisasterAid(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String,
        @Body aidRequest: UpdateDisasterAidRequest
    ): Response<DisasterAid>

    /**
     * Hapus bantuan tertentu
     */
    @DELETE("disasters/{disasterId}/aids/{aidId}")
    suspend fun deleteDisasterAid(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String
    ): Response<Unit>

    /**
     * Update status bantuan
     */
    @PATCH("disasters/{disasterId}/aids/{aidId}/status")
    suspend fun updateAidStatus(
        @Path("disasterId") disasterId: String,
        @Path("aidId") aidId: String,
        @Body statusRequest: Map<String, String>
    ): Response<DisasterAid>
}
