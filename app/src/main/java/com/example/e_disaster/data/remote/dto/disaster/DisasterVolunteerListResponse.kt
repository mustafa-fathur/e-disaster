package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class DisasterVolunteerDto(
    @SerializedName("user_id")
    val userId: String
)

data class DisasterVolunteerListResponse(
    @SerializedName("data")
    val data: List<DisasterVolunteerDto>
)
