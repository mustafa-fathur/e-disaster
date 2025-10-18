package com.example.e_disaster.data.model.home

data class Disaster(
    val id: String,
    val title: String,
    val type: String,
    val status: String,
    val location: String,
    val date: String
)

object DummyHomeData {
    val ongoingDisasters = 6
    val completedDisasters = 1

    val recentDisasters = listOf(
        Disaster("1", "Gempa Bumi M 6.2 di Jawa Barat", "earthquake", "ongoing", "Cianjur, Jawa Barat", "2025-10-16"),
        Disaster("2", "Banjir Bandang di Jakarta", "flood", "ongoing", "Jakarta Selatan", "2025-10-17"),
        Disaster("3", "Gunung Merapi Erupsi", "volcanic_eruption", "ongoing", "Sleman, Yogyakarta", "2025-10-17")
    )
}