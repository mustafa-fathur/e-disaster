package com.example.e_disaster.data.model

data class Disaster(
    val id: String?,
    val reportedBy: String?,
    val source: String?,
    val types: String?,
    val status: String?,
    val title: String?,
    val description: String?,
    val date: String?,
    val time: String?,
    val location: String?,
    val lat: Double?,
    val long: Double?,
    val magnitude: Double?,
    val depth: Double?,
    val createdAt: String?,
    val updatedAt: String?,
    val pictures: List<DisasterPicture>? = null
)

data class DisasterPicture(
    val id: String,
    val url: String,
    val caption: String?,
    val mimeType: String?
)
