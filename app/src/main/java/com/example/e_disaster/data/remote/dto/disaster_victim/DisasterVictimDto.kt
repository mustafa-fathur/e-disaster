package com.example.e_disaster.data.remote.dto.disaster_victim

import com.example.e_disaster.data.remote.dto.auth.UserDto
import com.google.gson.annotations.SerializedName

data class DisasterVictimDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("disaster_id")
    val disasterId: String?,
    @SerializedName("reported_by")
    val reportedBy: String?,
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
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("reporter")
    val reporter: ReporterDto?
)

data class ReporterDto(
    @SerializedName("user")
    val user: UserDto?
)
