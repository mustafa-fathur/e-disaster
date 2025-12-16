package com.example.e_disaster.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("fcm_token")
    val fcmToken: String? = null
)
