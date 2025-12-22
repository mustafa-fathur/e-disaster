package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class CreateDisasterResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: CreateDisasterData?
)

data class CreateDisasterData(
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

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("auto_assigned")
    val autoAssigned: Boolean?
)
