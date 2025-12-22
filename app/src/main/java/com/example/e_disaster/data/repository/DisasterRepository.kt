package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterPicture
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.service.DisasterApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterRepository @Inject constructor(
    private val apiService: DisasterApiService
) {

    suspend fun getDisasters(): List<Disaster> {
        val response = apiService.getDisasters()
        return response.data.map { mapDisasterDtoToDisaster(it) }
    }

    suspend fun getDisasterById(disasterId: String): Disaster {
        val response = apiService.getDisasterById(disasterId)
        return mapDisasterDtoToDisaster(response.data)
    }

    suspend fun joinDisaster(disasterId: String): String {
        val response = apiService.joinDisaster(disasterId)
        return response.message
    }

    suspend fun isUserAssigned(disasterId: String): Boolean {
        val response = apiService.checkAssignment(disasterId)
        return response.assigned
    }

    private fun mapDisasterDtoToDisaster(dto: DisasterDto): Disaster {
        return Disaster(
            id = dto.id ?: "",
            reportedBy = dto.reportedBy ?: "",
            source = dto.source ?: "",
            types = dto.types ?: "",
            status = dto.status ?: "",
            title = dto.title ?: "Tanpa Judul",
            description = dto.description ?: "Tidak ada deskripsi.",
            date = dto.date ?: "",
            time = dto.time ?: "",
            location = dto.location ?: "Lokasi tidak diketahui",
            lat = dto.lat,
            long = dto.long,
            magnitude = dto.magnitude,
            depth = dto.depth,
            createdAt = dto.createdAt ?: "",
            updatedAt = dto.updatedAt ?: "",
            pictures = dto.pictures?.mapNotNull { p ->
                val id = p.id ?: return@mapNotNull null
                val url = p.url ?: return@mapNotNull null
                DisasterPicture(
                    id = id,
                    url = url,
                    caption = p.caption,
                    mimeType = p.mimeType
                )
            }
        )
    }
}
