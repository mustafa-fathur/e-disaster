package com.example.e_disaster.data.remote.dto.disaster_report

import com.google.gson.annotations.SerializedName

data class DisasterReportDetailResponse(
    @SerializedName("data")
    val data: DisasterReportDto
)

