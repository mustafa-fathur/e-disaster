package com.example.e_disaster.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String,
    val user: UserDto,
    val token: String
)

data class ProfileResponse(
    val user: UserDto
)

data class PictureDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("url")
    val url: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val type: String,
    val status: String,
    val nik: String?,
    val phone: String?,
    val address: String?,
    val gender: Boolean?,
    @SerializedName("date_of_birth")
    val dateOfBirth: String?,
    @SerializedName("profile_picture")
    val profilePicture: PictureDto?
)
