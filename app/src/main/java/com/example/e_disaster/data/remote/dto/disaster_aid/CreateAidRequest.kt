package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class CreateAidRequest(
    val name: String,
    val category: String,
    val quantity: Int,
    val description: String,
    val location: String,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("long") val long: Double? = null
)