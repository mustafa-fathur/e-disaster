package com.example.e_disaster.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String,
    val nik: String,
    val phone: String,
    val address: String,
    val gender: Int,
    @SerializedName("date_of_birth")
    val dateOfBirth: String, // format "yyyy-MM-dd"
    @SerializedName("reason_to_join")
    val reasonToJoin: String
)
