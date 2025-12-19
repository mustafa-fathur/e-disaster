package com.example.e_disaster.data.remote.dto.disaster_report

import com.google.gson.annotations.SerializedName

data class UpdateDisasterReportRequest(
    val title: String,
    val description: String,
    @SerializedName("is_final_stage")
    val isFinalStage: Boolean
)