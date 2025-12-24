package com.example.e_disaster.data.remote.dto.notification

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("user_id")
    val userId: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("category")
    val category: String?,  // volunteer_verification, new_disaster, new_disaster_report, etc.

    @SerializedName("is_read")
    val isRead: Boolean?,

    @SerializedName("sent_at")
    val sentAt: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("data")
    val data: NotificationDataDto?
)

data class NotificationDataDto(
    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("report_id")
    val reportId: String?,

    @SerializedName("victim_id")
    val victimId: String?,

    @SerializedName("aid_id")
    val aidId: String?,

    @SerializedName("disaster_title")
    val disasterTitle: String?
)

