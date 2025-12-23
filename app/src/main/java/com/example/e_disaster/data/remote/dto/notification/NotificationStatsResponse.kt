package com.example.e_disaster.data.remote.dto.notification

import com.google.gson.annotations.SerializedName

data class NotificationStatsResponse(
    @SerializedName("data")
    val data: NotificationStatsDto?
)

data class NotificationStatsDto(
    @SerializedName("total")
    val total: Int?,

    @SerializedName("unread")
    val unread: Int?,

    @SerializedName("read")
    val read: Int?,

    @SerializedName("by_category")
    val byCategory: Map<String, Int>?
)

