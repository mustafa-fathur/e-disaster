package com.example.e_disaster.data.remote.dto.picture

import com.google.gson.annotations.SerializedName

data class PictureListResponse(
    @SerializedName("data")
    val data: List<PictureDto>
)

data class PictureDto(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("caption") val caption: String?,
    @SerializedName("mime_type") val mimeType: String?
)

