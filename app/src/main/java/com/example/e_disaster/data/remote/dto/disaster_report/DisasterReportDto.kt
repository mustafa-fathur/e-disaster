package com.example.e_disaster.data.remote.dto.disaster_report

import com.google.gson.annotations.SerializedName

data class DisasterReportDto(
    @SerializedName("id") val id: String?,
    @SerializedName("disaster_id") val disasterId: String?,
    @SerializedName("disaster_title") val disasterTitle: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("is_final_stage") val isFinalStage: Boolean = false,
    @SerializedName("reported_by") val reportedBy: String?,
    @SerializedName("reporter_name") val reporterName: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

