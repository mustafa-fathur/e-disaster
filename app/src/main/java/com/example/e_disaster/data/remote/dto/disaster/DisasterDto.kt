package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName
import com.example.e_disaster.data.remote.dto.picture.PictureDto

data class DisasterDto(
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

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("long")
    val long: Double?,

    @SerializedName("magnitude")
    val magnitude: Double?,

    @SerializedName("depth")
    val depth: Double?,

    @SerializedName("pictures")
    val pictures: List<PhotoDto>?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

// Local DTO alias matching the picture keys returned by the disaster controller
data class PhotoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("caption") val caption: String?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("mine_type") val mimeType: String?,
    @SerializedName("alt_text") val altText: String?,
    @SerializedName("created_at") val createdAt: String?
)
