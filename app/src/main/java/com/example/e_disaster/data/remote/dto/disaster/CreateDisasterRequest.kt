package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class CreateDisasterRequest(
    @SerializedName("title") val title: String,
    @SerializedName("source") val source: String,
    @SerializedName("type") val type: String,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("status") val status: String? = "ongoing",
    @SerializedName("location") val location: String? = null,
    @SerializedName("coordinate") val coordinate: String? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("long") val long: Double? = null,
    @SerializedName("magnitude") val magnitude: Double? = null,
    @SerializedName("depth") val depth: Double? = null
)
