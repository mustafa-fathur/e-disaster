package com.example.e_disaster.utils

import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterPicture
import com.example.e_disaster.utils.Constants.BASE_URL

object DisasterImageProvider {

    private const val PLACEHOLDER_URL = "https://picsum.photos/seed/disaster/800/600"

    fun getImageUrl(disaster: Disaster?): String {
        if (disaster == null) return PLACEHOLDER_URL
        val pics: List<DisasterPicture>? = disaster.pictures
        if (!pics.isNullOrEmpty()) {
            val first = pics.firstOrNull()
            val firstUrl = first?.url
            if (!firstUrl.isNullOrBlank()) {
                return if (firstUrl.startsWith("http://") || firstUrl.startsWith("https://")) firstUrl else "$BASE_URL$firstUrl"
            }
        }
        return PLACEHOLDER_URL
    }

    fun getImageUrlFromString(imageUrl: String?): String {
        return if (!imageUrl.isNullOrBlank()) imageUrl else PLACEHOLDER_URL
    }

    fun getImageUrls(disaster: Disaster?): List<String> {
        if (disaster == null) return listOf(PLACEHOLDER_URL)
        val pics = disaster.pictures
        if (!pics.isNullOrEmpty()) {
            return pics.mapNotNull { pic ->
                val u = pic.url
                if (u.isNullOrBlank()) null else {
                    if (u.startsWith("http://") || u.startsWith("https://")) u else "$BASE_URL$u"
                }
            }.ifEmpty { listOf(PLACEHOLDER_URL) }
        }
        return listOf(PLACEHOLDER_URL)
    }
}
