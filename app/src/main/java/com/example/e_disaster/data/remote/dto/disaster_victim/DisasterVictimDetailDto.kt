package com.example.e_disaster.data.remote.dto.disaster_victim

import com.google.gson.annotations.SerializedName

data class DisasterVictimDetailResponse(
    @SerializedName("data")
    val data: DisasterVictimDetailDto
)

data class DisasterVictimDetailDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("disaster_title")
    val disasterTitle: String?,

    @SerializedName("nik")
    val nik: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("date_of_birth")
    val dateOfBirth: String?,

    @SerializedName("gender")
    val gender: Boolean?,

    @SerializedName("contact_info")
    val contactInfo: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("is_evacuated")
    val isEvacuated: Boolean?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("reported_by")
    val reportedBy: String?,

    @SerializedName("reporter_name")
    val reporterName: String?,

    @SerializedName("pictures")
    val pictures: List<VictimPictureDto>?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

data class VictimPictureDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("file_path")
    val filePath: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("mine_type")
    val mimeType: String?,

    @SerializedName("alt_text")
    val altText: String?,

    @SerializedName("created_at")
    val createdAt: String?
)