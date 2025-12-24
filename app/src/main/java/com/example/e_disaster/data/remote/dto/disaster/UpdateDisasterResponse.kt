package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class UpdateDisasterResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: DisasterDto?
)
