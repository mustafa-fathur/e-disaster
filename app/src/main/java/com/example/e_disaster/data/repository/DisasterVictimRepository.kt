package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDetailDto
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDto
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterVictimRepository @Inject constructor(
    private val apiService: DisasterVictimApiService
) {
    suspend fun getDisasterVictims(disasterId: String): List<DisasterVictim> {
        val response = apiService.getDisasterVictims(disasterId)
        return response.data.map { mapVictimDtoToVictim(it, disasterId) }
    }

    suspend fun getDisasterVictimDetail(
        disasterId: String,
        victimId: String
    ): DisasterVictim {
        val response = apiService.getDisasterVictimDetail(disasterId, victimId)
        return mapVictimDetailDtoToVictim(response.data)
    }

    private fun mapVictimDtoToVictim(dto: DisasterVictimDto, disasterId: String): DisasterVictim {
        return DisasterVictim(
            id = dto.id ?: "",
            nik = dto.nik ?: "N/A",
            name = dto.name ?: "Tanpa Nama",
            dateOfBirth = dto.dateOfBirth ?: "",
            createdAt = dto.createdAt ?: "",
            gender = if (dto.gender == true) "Perempuan" else "Laki-laki",
            contactInfo = dto.contactInfo ?: "N/A",
            description = dto.description ?: "Tidak ada deskripsi",
            isEvacuated = dto.isEvacuated ?: false,
            status = dto.status ?: "unknown",
            reporterName = dto.reporter?.user?.name ?: "N/A",
            disasterId = disasterId
        )
    }

    private fun mapVictimDetailDtoToVictim(dto: DisasterVictimDetailDto): DisasterVictim {
        return DisasterVictim(
            id = dto.id ?: "",
            nik = dto.nik ?: "N/A",
            name = dto.name ?: "Tanpa Nama",
            dateOfBirth = dto.dateOfBirth ?: "",
            gender = if (dto.gender == true) "Perempuan" else "Laki-laki",
            contactInfo = dto.contactInfo ?: "N/A",
            description = dto.description ?: "Tidak ada deskripsi",
            isEvacuated = dto.isEvacuated ?: false,
            status = dto.status ?: "unknown",
            createdAt = dto.createdAt ?: "",
            reporterName = dto.reporterName ?: "N/A",
            disasterId = dto.disasterId,
            disasterTitle = dto.disasterTitle,
            reportedBy = dto.reportedBy,
            pictures = dto.pictures?.map { pictureDto ->
                VictimPicture(
                    id = pictureDto.id ?: "",
                    url = pictureDto.url ?: "",
                    caption = pictureDto.caption,
                    mimeType = pictureDto.mimeType ?: "image/jpeg"
                )
            },
            updatedAt = dto.updatedAt
        )
    }
}
