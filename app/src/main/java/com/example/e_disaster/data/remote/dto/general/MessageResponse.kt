package com.example.e_disaster.data.remote.dto.general

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message")
    val message: String)
