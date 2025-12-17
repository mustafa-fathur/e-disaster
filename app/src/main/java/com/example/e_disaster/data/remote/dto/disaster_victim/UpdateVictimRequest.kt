package com.example.e_disaster.data.remote.dto.disaster_victim

import com.google.gson.annotations.SerializedName

data class UpdateVictimRequest(
    @SerializedName("nik")
    val nik: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("gender")
    val gender: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("is_evacuated")
    val isEvacuated: Boolean,
    @SerializedName("contact_info")
    val contactInfo: String?,
    @SerializedName("description")
    val description: String?
)