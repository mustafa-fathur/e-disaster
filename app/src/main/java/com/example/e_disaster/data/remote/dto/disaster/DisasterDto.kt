package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class DisasterDto(
    val id: String,
    val name: String?,
    val location: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("long") val long: Double?
)