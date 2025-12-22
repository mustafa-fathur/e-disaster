package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class UpdateDisasterResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: UpdateDisasterData?
)

data class UpdateDisasterData(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("time")
    val time: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)
