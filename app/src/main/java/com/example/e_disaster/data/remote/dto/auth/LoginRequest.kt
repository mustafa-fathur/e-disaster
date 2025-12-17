package com.example.e_disaster.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("fcm_token")
    val fcmToken: String? = null,
    val platform: String? = "android",
    @SerializedName("device_id")
    val deviceId: String? = null,
    @SerializedName("device_name")
    val deviceName: String? = null,
    @SerializedName("app_version")
    val appVersion: String? = null
)
