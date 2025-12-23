package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class DisasterAidDetailResponse(
    @SerializedName("data")
    val data: DisasterAidDto?
)

