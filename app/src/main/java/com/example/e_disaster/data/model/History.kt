package com.example.e_disaster.data.model

data class Participant(
    val id: String,
    val name: String,
    val role: String, // "officer" atau "volunteer"
    val profileImageUrl: String
)

data class History(
    val id: String,
    val disasterName: String,
    val location: String,
    val date: String,
    val description: String,
    val imageUrl: String,
    val status: String,
    val participants: List<Participant> = emptyList()
)