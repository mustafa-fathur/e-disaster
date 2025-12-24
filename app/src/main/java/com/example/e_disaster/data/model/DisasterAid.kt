package com.example.e_disaster.data.model

data class DisasterAid(
    val id: String,
    val disasterId: String,
    val reportedBy: String,
    val donator: String,
    val location: String,
    val title: String,
    val description: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val createdAt: String,
    val updatedAt: String,
    // Field tambahan untuk kebutuhan UI
    val disasterTitle: String? = null,
    val disasterLocation: String? = null,
    val reporterName: String? = null,
    val pictures: List<VictimPicture>? = null  // Reuse VictimPicture model
)
