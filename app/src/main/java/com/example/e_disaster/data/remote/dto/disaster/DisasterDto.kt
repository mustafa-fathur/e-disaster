package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class DisasterDto(
    val id: String,
    val title: String,
    val description: String?,
    val location: String?,
    val status: String,

    @SerializedName("types")
    val disasterCategory: String,

    @SerializedName("date")
    val disasterDate: String,
)