package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.service.DisasterApiService
import retrofit2.HttpException
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
        return mapDisasterDtoToDisaster(response)
    }

    suspend fun checkAssignmentStatus(disasterId: String): Boolean {
        return try {
            apiService.getDisasterReports(disasterId)
            true
        } catch (e: HttpException) {
            if (e.code() == 403) {
                false
            } else {
                throw e
            }
        }
    }

    suspend fun joinDisaster(disasterId: String) {
        apiService.joinDisaster(disasterId)
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
            updatedAt = dto.updatedAt ?: ""
        )
    }
}
