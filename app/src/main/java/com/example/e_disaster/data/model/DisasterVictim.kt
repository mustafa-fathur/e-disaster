package com.example.e_disaster.data.model

data class DisasterVictim(
    val id: String,
    val nik: String,
    val name: String,
    val dateOfBirth: String,
    val gender: String,
    val contactInfo: String,
    val description: String,
    val isEvacuated: Boolean,
    val status: String,
    val createdAt: String,
    val reporterName: String
)
