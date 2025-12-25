package com.example.e_disaster.data.repository

import com.example.e_disaster.data.model.DisasterReport
import com.example.e_disaster.data.model.ReportPicture
import com.example.e_disaster.data.remote.dto.disaster_report.CreateDisasterReportRequest
import com.example.e_disaster.data.remote.dto.disaster_report.DisasterReportDto
import com.example.e_disaster.data.remote.dto.disaster_report.UpdateDisasterReportRequest
import com.example.e_disaster.data.remote.service.DisasterReportApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterReportRepository @Inject constructor(
    private val apiService: DisasterReportApiService,
    private val pictureApiService: PictureApiService
) {

    suspend fun getDisasterReports(disasterId: String): List<DisasterReport> {
        val response = apiService.getDisasterReports(disasterId)
        return response.data.map { mapDisasterReportDtoToModel(it) }
    }

    suspend fun getDisasterReport(
        disasterId: String,
        reportId: String
    ): DisasterReport {
        val response = apiService.getDisasterReport(disasterId, reportId)
        val report = mapDisasterReportDtoToModel(response.data)

        try {
            val picturesResponse = pictureApiService.getPictures("disaster_report", reportId)
            val pictures = picturesResponse.data.mapNotNull { dto ->
                val id = dto.id ?: return@mapNotNull null
                val url = dto.url ?: return@mapNotNull null
                ReportPicture(
                    id = id,
                    url = url,
                    caption = dto.caption,
                    mimeType = dto.mimeType ?: "image/jpeg"
                )
            }
            return report.copy(pictures = pictures)
        } catch (e: Exception) {
            e.printStackTrace()
            return report
        }
    }

    suspend fun createDisasterReport(
        disasterId: String,
        title: String,
        description: String,
        images: List<File>,
        isFinalStage: Boolean?,
        lat: Double?,
        long: Double?
    ): DisasterReport {
        val createReportRequest = CreateDisasterReportRequest(
            title = title,
            description = description,
            isFinalStage = isFinalStage,
            lat = lat,
            long = long
        )

        val reportResponse = apiService.createDisasterReport(
            disasterId = disasterId,
            request = createReportRequest
        )

        val createdReport = reportResponse.data

        if (images.isNotEmpty()) {
            val newReportId = createdReport.id
            if (newReportId != null) {
                val imageParts = images.map {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images[]", it.name, requestFile)
                }
                apiService.uploadReportImages(
                    reportId = newReportId,
                    images = imageParts
                )
            }
        }

        return mapDisasterReportDtoToModel(createdReport)
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
