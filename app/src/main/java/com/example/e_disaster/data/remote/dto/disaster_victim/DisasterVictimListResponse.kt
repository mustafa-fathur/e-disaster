package com.example.e_disaster.data.remote.dto.disaster_victim

import com.google.gson.annotations.SerializedName
class DisasterVictimListResponse(
    @SerializedName("data")
    val data: List<DisasterVictimDto>
)