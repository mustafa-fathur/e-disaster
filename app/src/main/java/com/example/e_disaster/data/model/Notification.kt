package com.example.e_disaster.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("type")
    val type: String, // disaster, aid, victim, system

    @SerializedName("priority")
    val priority: String, // low, medium, high, urgent

    @SerializedName("is_read")
    val isRead: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("data")
    val data: NotificationData? = null
)

data class NotificationData(
    @SerializedName("disaster_id")
    val disasterId: String? = null,

    @SerializedName("disaster_name")
    val disasterName: String? = null,

    @SerializedName("aid_id")
    val aidId: String? = null,

    @SerializedName("aid_name")
    val aidName: String? = null,

    @SerializedName("victim_id")
    val victimId: String? = null,

    @SerializedName("victim_name")
    val victimName: String? = null
)

enum class NotificationType(val value: String) {
    DISASTER("disaster"),
    AID("aid"),
    VICTIM("victim"),
    SYSTEM("system")
}

enum class NotificationPriority(val value: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    URGENT("urgent")
}
