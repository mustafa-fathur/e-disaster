package com.example.e_disaster.data.remote.dto.dashboard

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("user")
    val user: DashboardUser?,

    @SerializedName("stats")
    val stats: DashboardStats?,

    @SerializedName("recent_disasters")
    val recentDisasters: List<RecentDisaster>?,

    @SerializedName("recent_reports")
    val recentReports: List<RecentReport>?,

    @SerializedName("recent_victims")
    val recentVictims: List<RecentVictim>?,

    @SerializedName("recent_aids")
    val recentAids: List<RecentAid>?,

    @SerializedName("unread_notifications")
    val unreadNotifications: Int?
)

data class DashboardUser(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("status")
    val status: String?
)

data class DashboardStats(
    @SerializedName("total_disasters")
    val totalDisasters: Int?,

    @SerializedName("assigned_disasters")
    val assignedDisasters: Int?,

    @SerializedName("ongoing_disasters")
    val ongoingDisasters: Int?,

    @SerializedName("completed_disasters")
    val completedDisasters: Int?,

    @SerializedName("total_reports")
    val totalReports: Int?,

    @SerializedName("total_victims")
    val totalVictims: Int?,

    @SerializedName("total_aids")
    val totalAids: Int?
)

data class RecentDisaster(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("time")
    val time: String?,

    @SerializedName("created_at")
    val createdAt: String?
)

data class RecentReport(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("disaster_title")
    val disasterTitle: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("is_final_stage")
    val isFinalStage: Boolean?,

    @SerializedName("created_at")
    val createdAt: String?
)

data class RecentVictim(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("is_evacuated")
    val isEvacuated: Boolean?,

    @SerializedName("disaster_title")
    val disasterTitle: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("created_at")
    val createdAt: String?
)

data class RecentAid(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("quantity")
    val quantity: Int?,

    @SerializedName("unit")
    val unit: String?,

    @SerializedName("disaster_title")
    val disasterTitle: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("created_at")
    val createdAt: String?
)
