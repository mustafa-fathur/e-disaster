package com.example.e_disaster.data.repository

import android.content.Context
import android.net.Uri
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.data.remote.dto.disaster_victim.AddVictimResponse
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDetailDto
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDto
import com.example.e_disaster.data.remote.dto.disaster_victim.UpdateVictimRequest
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import com.example.e_disaster.ui.features.disaster_victim.add.AddVictimUiState
import com.example.e_disaster.ui.features.disaster_victim.update.UpdateVictimUiState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterVictimRepository @Inject constructor(
    private val apiService: DisasterVictimApiService,
    private val pictureApiService: PictureApiService
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

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = mimeType.substringAfter('/')
        val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.$extension")

        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return tempFile
    }

    suspend fun addDisasterVictim(
        disasterId: String,
        uiState: AddVictimUiState,
        context: Context
    ): AddVictimResponse {
        val nikPart = uiState.nik.toRequestBody("text/plain".toMediaTypeOrNull())
        val namePart = uiState.name.toRequestBody("text/plain".toMediaTypeOrNull())

        val dobPart: RequestBody = if (uiState.dob.isNotBlank()) {
            try {
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(uiState.dob, inputFormatter)
                val formattedDate = date.format(outputFormatter)
                formattedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            } catch (e: Exception) {
                "".toRequestBody("text/plain".toMediaTypeOrNull())
            }
        } else {
            "".toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val genderValue = if (uiState.gender == "Perempuan") "1" else "0"
        val genderPart = genderValue.toRequestBody("text/plain".toMediaTypeOrNull())

        val statusApiValue = when (uiState.victimStatus) {
            "Luka Ringan" -> "minor_injury"
            "Luka Berat" -> "serious_injuries"
            "Meninggal" -> "deceased"
            "Hilang" -> "lost"
            else -> ""
        }
        val statusPart = statusApiValue.toRequestBody("text/plain".toMediaTypeOrNull())

        val isEvacuatedValue = if (uiState.isEvacuated) "1" else "0"
        val isEvacuatedPart =
            isEvacuatedValue.toRequestBody("text/plain".toMediaTypeOrNull())

        val contactPart =
            uiState.contact.ifBlank { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart =
            uiState.description.ifBlank { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

        val imageParts = uiState.images.mapNotNull { uri ->
            val file = getFileFromUri(context, uri) ?: return@mapNotNull null
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

            MultipartBody.Part.createFormData("images[]", file.name, requestBody)
        }

        val url = "disasters/$disasterId/victims"

        return apiService.addDisasterVictim(
            url = url,
            nik = nikPart,
            name = namePart,
            dateOfBirth = dobPart,
            gender = genderPart,
            status = statusPart,
            isEvacuated = isEvacuatedPart,
            contactInfo = contactPart,
            description = descriptionPart,
            images = imageParts.ifEmpty { null }
        )
    }

    suspend fun updateDisasterVictim(
        disasterId: String,
        victimId: String,
        uiState: UpdateVictimUiState
    ): AddVictimResponse {
        val formattedDate = try {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(uiState.dob, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            uiState.dob
        }

        val statusApiValue = when (uiState.victimStatus) {
            "Luka Ringan" -> "minor_injury"
            "Luka Berat" -> "serious_injuries"
            "Meninggal" -> "deceased"
            "Hilang" -> "lost"
            else -> uiState.victimStatus
        }

        val requestBody = UpdateVictimRequest(
            nik = uiState.nik,
            name = uiState.name,
            dateOfBirth = formattedDate,
            gender = uiState.gender == "Perempuan",
            status = statusApiValue,
            isEvacuated = uiState.isEvacuated,
            contactInfo = uiState.contact.ifBlank { null },
            description = uiState.description.ifBlank { null }
        )

        return apiService.updateDisasterVictim(disasterId, victimId, requestBody)
    }

    suspend fun deleteDisasterVictim(disasterId: String, victimId: String) {
        apiService.deleteDisasterVictim(disasterId, victimId)
    }

    suspend fun addVictimPicture(victimId: String, imageUri: Uri, context: Context) {
        val file = getFileFromUri(context, imageUri) ?: throw Exception("Gagal memproses file")
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        pictureApiService.uploadPicture(
            modelType = "disaster_victim",
            modelId = victimId,
            image = imagePart
        )
    }

    suspend fun deleteVictimPicture(victimId: String, pictureId: String) {
        pictureApiService.deletePicture(
            modelType = "disaster_victim",
            modelId = victimId,
            pictureId = pictureId
        )
    }
}
