package com.example.e_disaster.data.remote.dto.notification

import com.google.gson.annotations.SerializedName

data class NotificationDetailResponse(
    @SerializedName("data")
    val data: NotificationDto?
)

