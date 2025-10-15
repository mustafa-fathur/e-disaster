package com.example.e_disaster.data.model

import com.google.gson.annotations.SerializedName

data class DisasterAid(
    @SerializedName("id")
    val id: String,

    @SerializedName("disaster_id")
    val disasterId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String, // makanan, obat, tenda, pakaian, dll

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unit")
    val unit: String, // kg, liter, buah, dll

    @SerializedName("status")
    val status: String, // tersedia, habis, dalam_perjalanan, terdistribusi

    @SerializedName("location")
    val location: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("contact_person")
    val contactPerson: String?,

    @SerializedName("contact_phone")
    val contactPhone: String?,

    @SerializedName("estimated_arrival")
    val estimatedArrival: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    // Field tambahan untuk kebutuhan UI
    val distance: Double? = null, // Jarak dari lokasi pengguna (km)
    val disasterName: String? = null,
    val disasterLocation: String? = null
)

data class CreateDisasterAidRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unit")
    val unit: String,

    @SerializedName("location")
    val location: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("contact_person")
    val contactPerson: String?,

    @SerializedName("contact_phone")
    val contactPhone: String?,

    @SerializedName("estimated_arrival")
    val estimatedArrival: String?
)

data class UpdateDisasterAidRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("unit")
    val unit: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("contact_person")
    val contactPerson: String? = null,

    @SerializedName("contact_phone")
    val contactPhone: String? = null,

    @SerializedName("estimated_arrival")
    val estimatedArrival: String? = null
)

// Enum untuk tipe bantuan
enum class AidType(val value: String) {
    MAKANAN("makanan"),
    MINUMAN("minuman"),
    OBAT("obat"),
    PAKAIAN("pakaian"),
    TENDA("tenda"),
    SELIMUT("selimut"),
    ALAT_MEDIS("alat_medis"),
    AIR_BERSIH("air_bersih"),
    LISTRIK("listrik"),
    LAINNYA("lainnya")
}

// Enum untuk status bantuan
enum class AidStatus(val value: String) {
    TERSEDIA("tersedia"),
    HABIS("habis"),
    DALAM_PERJALANAN("dalam_perjalanan"),
    TERDISTRIBUSI("terdistribusi")
}
