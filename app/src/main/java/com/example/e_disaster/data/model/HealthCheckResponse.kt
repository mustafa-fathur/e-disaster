package com.example.e_disaster.data.model

import com.google.gson.annotations.SerializedName

data class HealthCheckResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)