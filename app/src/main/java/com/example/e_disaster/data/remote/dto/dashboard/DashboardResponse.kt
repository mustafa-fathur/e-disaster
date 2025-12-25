package com.example.e_disaster.data.remote.dto.dashboard

import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster_aid.AidDto
import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("user") val user: DashboardUserDto?,
    @SerializedName("stats") val stats: DashboardStatsDto?,
    @SerializedName("recent_disasters") val recentDisasters: Map<String, DisasterDto>?,
    @SerializedName("recent_reports") val recentReports: List<DashboardReportDto>?,
    @SerializedName("recent_victims") val recentVictims: List<DashboardVictimDto>?,
    @SerializedName("recent_aids") val recentAids: List<AidDto>?,
    @SerializedName("unread_notifications") val unreadNotifications: Int?
)

data class DashboardDataWrapper<T>(
    @SerializedName("data") val data: List<T>?
)

data class DashboardUserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?
)

data class DashboardStatsDto(
    @SerializedName("total_disasters") val totalDisasters: Int?,
    @SerializedName("assigned_disasters") val assignedDisasters: Int?,
    @SerializedName("ongoing_disasters") val ongoingDisasters: Int?,
    @SerializedName("completed_disasters") val completedDisasters: Int?,
    @SerializedName("total_reports") val totalReports: Int?,
    @SerializedName("total_victims") val totalVictims: Int?,
    @SerializedName("total_aids") val totalAids: Int?
)

data class DashboardReportDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("disaster_title") val disasterTitle: String?,
    @SerializedName("disaster_id") val disasterId: String?,
    @SerializedName("is_final_stage") val isFinalStage: Boolean?,
    @SerializedName("created_at") val createdAt: String?
)

data class DashboardVictimDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("is_evacuated") val isEvacuated: Boolean?,
    @SerializedName("disaster_title") val disasterTitle: String?,
    @SerializedName("disaster_id") val disasterId: String?,
    @SerializedName("created_at") val createdAt: String?
)
