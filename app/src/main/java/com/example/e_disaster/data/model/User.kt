package com.example.e_disaster.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val userType: String, // e.g., "officer", "volunteer"
    val status: String,
    val nik: String,
    val phone: String,
    val address: String,
    val dateOfBirth: String,
    val gender: String,
    val profilePicture: String
)
    