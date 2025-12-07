package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DisasterVictimApiService {

    @GET("disasters/{disasterId}/victims")
    suspend fun getDisasterVictims(
        @Path("disasterId") disasterId: String
    ): DisasterVictimListResponse
}