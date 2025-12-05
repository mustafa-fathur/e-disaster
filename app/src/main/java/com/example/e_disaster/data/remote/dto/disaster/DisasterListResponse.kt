package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName
data class DisasterListResponse(
    @SerializedName("data")
    val data: List<DisasterDto>
)
