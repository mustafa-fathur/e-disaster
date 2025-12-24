package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class DisasterDetailResponse(
    @SerializedName("data")
    val data: DisasterDto
)
