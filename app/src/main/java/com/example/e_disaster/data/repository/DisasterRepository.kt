package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterReport
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.remote.dto.disaster_report.CreateDisasterReportRequest
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportDto
import com.example.e_disaster.data.remote.dto.disaster_report.UpdateDisasterReportRequest
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

    suspend fun joinDisaster(disasterId: String): String {
        val response = apiService.joinDisaster(disasterId)
        return response.message
    }

    suspend fun isUserAssigned(disasterId: String): Boolean {
        return try {
            apiService.getDisasterReports(disasterId)
            true
        } catch (e: HttpException) {
            if (e.code() == 403) false else throw e
        }
    }

    suspend fun getDisasterReports(disasterId: String): List<DisasterReport> {
        val response = apiService.getDisasterReports(disasterId)
        return response.data.map { mapDisasterReportDtoToModel(it) }
    }

    suspend fun getDisasterReport(
        disasterId: String,
        reportId: String
    ): DisasterReport {
        val response = apiService.getDisasterReport(disasterId, reportId)
        return mapDisasterReportDtoToModel(response.data)
    }

    suspend fun createDisasterReport(
        disasterId: String,
        request: CreateDisasterReportRequest
    ): DisasterReport {
        val response = apiService.createDisasterReport(disasterId, request)
        return mapDisasterReportDtoToModel(response.data)
    }

    suspend fun updateDisasterReport(
        disasterId: String,
        reportId: String,
        title: String,
        description: String,
        isFinalStage: Boolean
    ): DisasterReport {
        val request = UpdateDisasterReportRequest(
            title = title,
            description = description,
            isFinalStage = isFinalStage
        )
        val response = apiService.updateDisasterReport(disasterId, reportId, request)
        return mapDisasterReportDtoToModel(response.data)
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

    private fun mapDisasterReportDtoToModel(dto: DisasterReportDto): DisasterReport {
        return DisasterReport(
            id = dto.id ?: "",
            disasterId = dto.disasterId ?: "",
            disasterTitle = dto.disasterTitle ?: "",
            title = dto.title ?: "",
            description = dto.description ?: "",
            isFinalStage = dto.isFinalStage,
            reportedBy = dto.reportedBy ?: "",
            reporterName = dto.reporterName ?: "",
            createdAt = dto.createdAt ?: "",
            updatedAt = dto.updatedAt ?: ""
        )
    }
}
