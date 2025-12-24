package com.example.e_disaster.data.remote.dto.disaster

import com.google.gson.annotations.SerializedName

data class DisasterDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("reported_by")
    val reportedBy: String?,

    @SerializedName("source")
    val source: String?,
    
    // PERBAIKAN: Mengubah "types" menjadi "type" sesuai respons API
    @SerializedName("type")
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

data class PhotoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("caption") val caption: String?,
    @SerializedName("file_path") val filePath: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("mine_type") val mimeType: String?,
    @SerializedName("alt_text") val altText: String?,
    @SerializedName("created_at") val createdAt: String?
)
