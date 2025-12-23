package com.example.e_disaster.data.remote.dto.disaster_aid

import com.google.gson.annotations.SerializedName

data class UpdateAidRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("quantity")
    val quantity: Int,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("donator")
    val donator: String,
    
    @SerializedName("location")
    val location: String
)
