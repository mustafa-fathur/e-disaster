package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class AidDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("disaster_title") val disasterTitle: String?,
    @SerializedName("disaster_id") val disasterId: String?,
    @SerializedName("created_at") val createdAt: String?
)
