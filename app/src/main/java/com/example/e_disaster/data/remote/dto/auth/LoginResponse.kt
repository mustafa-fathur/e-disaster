package com.example.e_disaster.data.remote.dto.auth

data class LoginResponse(
    val message: String,
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val type: String,
    val status: String
)
    