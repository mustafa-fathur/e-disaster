package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class UpdateDisasterRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("source")
    val source: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("coordinate")
    val coordinate: String? = null,

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("long")
    val long: Double? = null,

    @SerializedName("magnitude")
    val magnitude: Double? = null,

    @SerializedName("depth")
    val depth: Double? = null
)
