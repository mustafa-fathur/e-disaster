package com.example.e_disaster.data.model

data class DisasterReport(
    val id: String,
    val disasterId: String,
    val disasterTitle: String,
    val title: String,
    val description: String,
    val isFinalStage: Boolean,
    val reportedBy: String,
    val reporterName: String,
    val createdAt: String,
    val updatedAt: String
)

