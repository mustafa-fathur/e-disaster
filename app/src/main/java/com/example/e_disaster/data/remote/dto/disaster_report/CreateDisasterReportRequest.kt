package com.example.e_disaster.data.remote.dto.disaster_report

import com.google.gson.annotations.SerializedName

data class CreateDisasterReportRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("is_final_stage") val isFinalStage: Boolean?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("long") val long: Double?
)
