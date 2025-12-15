package com.example.e_disaster.data.remote.dto.disaster_victim

import com.google.gson.annotations.SerializedName

data class AddVictimResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: DisasterVictimDto?
)