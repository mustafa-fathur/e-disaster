package com.example.e_disaster.data.remote.dto.disaster_report

import com.google.gson.annotations.SerializedName

data class DisasterReportListResponse(
    @SerializedName("data")
    val data: List<Any>
)