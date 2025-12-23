package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class CreateAidResponse(
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: DisasterAidDto?
)

