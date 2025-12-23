package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class DisasterAidListResponse(
    @SerializedName("data")
    val data: List<DisasterAidDto>?,

    @SerializedName("pagination")
    val pagination: PaginationDto?
)

data class PaginationDto(
    @SerializedName("current_page")
    val currentPage: Int?,

    @SerializedName("per_page")
    val perPage: Int?,

    @SerializedName("total")
    val total: Int?,

    @SerializedName("last_page")
    val lastPage: Int?,

    @SerializedName("from")
    val from: Int?,

    @SerializedName("to")
    val to: Int?
)

