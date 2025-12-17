package com.example.e_disaster.data.remote.service

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PictureApiService {

    @Multipart
    @POST("pictures/{modelType}/{modelId}")
    suspend fun uploadPicture(
        @Path("modelType") modelType: String,
        @Path("modelId") modelId: String,
        @Part image: MultipartBody.Part
    ): Response<Unit>

    @DELETE("pictures/{modelType}/{modelId}/{pictureId}")
    suspend fun deletePicture(
        @Path("modelType") modelType: String,
        @Path("modelId") modelId: String,
        @Path("pictureId") pictureId: String
    ): Response<Unit>
}