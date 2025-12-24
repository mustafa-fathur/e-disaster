package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class DisasterAidDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("reported_by")
    val reportedBy: String?,

    @SerializedName("donator")
    val donator: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("category")
    val category: String?,

    @SerializedName("quantity")
    val quantity: Int?,

    @SerializedName("unit")
    val unit: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("disaster")
    val disaster: AidDisasterDto?,

    @SerializedName("reporter")
    val reporter: AidReporterDto?,

    @SerializedName("pictures")
    val pictures: List<AidPictureDto>?
)

data class AidDisasterDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("reported_by")
    val reportedBy: String?,

    @SerializedName("source")
    val source: String?,

    @SerializedName("types")
    val types: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("date")
    val date: String?,

    @SerializedName("time")
    val time: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("coordinate")
    val coordinate: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("long")
    val long: Double?,

    @SerializedName("magnitude")
    val magnitude: Double?,

    @SerializedName("depth")
    val depth: Double?,

    @SerializedName("cancelled_reason")
    val cancelledReason: String?,

    @SerializedName("cancelled_at")
    val cancelledAt: String?,

    @SerializedName("cancelled_by")
    val cancelledBy: String?,

    @SerializedName("completed_at")
    val completedAt: String?,

    @SerializedName("completed_by")
    val completedBy: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

data class AidReporterDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("disaster_id")
    val disasterId: String?,

    @SerializedName("user_id")
    val userId: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("deleted_at")
    val deletedAt: String?,

    @SerializedName("user")
    val user: AidUserDto?
)

data class AidUserDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("email_verified")
    val emailVerified: Boolean?,

    @SerializedName("two_factor_secret")
    val twoFactorSecret: String?,

    @SerializedName("two_factor_recovery_codes")
    val twoFactorRecoveryCodes: String?,

    @SerializedName("two_factor_confirmed_at")
    val twoFactorConfirmedAt: String?,

    @SerializedName("password_changed_at")
    val passwordChangedAt: String?,

    @SerializedName("timezone")
    val timezone: String?,

    @SerializedName("last_login_at")
    val lastLoginAt: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("coordinate")
    val coordinate: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("long")
    val long: Double?,

    @SerializedName("nik")
    val nik: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("gender")
    val gender: Boolean?,

    @SerializedName("date_of_birth")
    val dateOfBirth: String?,

    @SerializedName("reason_to_join")
    val reasonToJoin: String?,

    @SerializedName("registered_at")
    val registeredAt: String?,

    @SerializedName("approved_at")
    val approvedAt: String?,

    @SerializedName("approved_by")
    val approvedBy: String?,

    @SerializedName("rejection_reason")
    val rejectionReason: String?,

    @SerializedName("rejected_by")
    val rejectedBy: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("deleted_at")
    val deletedAt: String?
)

data class AidPictureDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("file_path")
    val filePath: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("mine_type")
    val mimeType: String?,

    @SerializedName("alt_text")
    val altText: String?,

    @SerializedName("created_at")
    val createdAt: String?
)

