package com.example.e_disaster.data.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.data.remote.dto.disaster_aid.CreateAidResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidDetailResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidDto
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidListResponse
import com.example.e_disaster.data.remote.dto.disaster_aid.UpdateAidRequest
import com.example.e_disaster.data.remote.service.DisasterAidApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterAidRepository @Inject constructor(
    private val apiService: DisasterAidApiService,
    private val pictureApiService: PictureApiService
) {

    suspend fun getDisasterAids(
        disasterId: String,
        page: Int? = null,
        perPage: Int? = null,
        search: String? = null,
        category: String? = null
    ): DisasterAidListResponse {
        return apiService.getDisasterAids(
            disasterId = disasterId,
            page = page,
            perPage = perPage,
            search = search,
            category = category
        )
    }

    suspend fun getDisasterAidById(
        disasterId: String,
        aidId: String
    ): DisasterAidDetailResponse {
        return apiService.getDisasterAidById(disasterId, aidId)
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        if (uri.scheme == "content" && uri.authority == "${context.packageName}.provider") {
            return uri.path?.let { File(context.cacheDir, it.substringAfter("my_cache/")) }
        }

        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        val tempFile = File(context.cacheDir, "upload_aid_${System.currentTimeMillis()}.$extension")

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

    suspend fun createDisasterAid(
        disasterId: String,
        title: String,
        category: String,
        quantity: Int,
        unit: String,
        description: String,
        donator: String,
        location: String,
        images: List<Uri>,
        context: Context
    ): CreateAidResponse {
        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())
        val quantityPart = quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val unitPart = unit.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val donatorPart = donator.toRequestBody("text/plain".toMediaTypeOrNull())
        val locationPart = location.toRequestBody("text/plain".toMediaTypeOrNull())

        val imageParts = images.mapIndexedNotNull { index, uri ->
            val file = getFileFromUri(context, uri) ?: return@mapIndexedNotNull null
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val fileName = "image_${System.currentTimeMillis()}_$index.${file.extension}"
            MultipartBody.Part.createFormData("images[]", fileName, requestBody)
        }

        val url = "disasters/$disasterId/aids"

        return apiService.createAid(
            url = url,
            title = titlePart,
            category = categoryPart,
            quantity = quantityPart,
            unit = unitPart,
            description = descriptionPart,
            donator = donatorPart,
            location = locationPart,
            images = imageParts.ifEmpty { null }
        )
    }

    suspend fun updateDisasterAid(
        disasterId: String,
        aidId: String,
        title: String,
        category: String,
        quantity: Int,
        unit: String,
        description: String,
        donator: String,
        location: String
    ): CreateAidResponse {
        val requestBody = UpdateAidRequest(
            title = title,
            category = category,
            quantity = quantity,
            unit = unit,
            description = description,
            donator = donator,
            location = location
        )

        return apiService.updateAid(disasterId, aidId, requestBody)
    }

    suspend fun deleteDisasterAid(disasterId: String, aidId: String) {
        apiService.deleteAid(disasterId, aidId)
    }

    suspend fun addAidPicture(aidId: String, imageUri: Uri, context: Context) {
        val file = getFileFromUri(context, imageUri) ?: throw Exception("Gagal memproses file")
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        pictureApiService.uploadPicture(
            modelType = "disaster_aid",
            modelId = aidId,
            image = imagePart
        )
    }

    suspend fun deleteAidPicture(aidId: String, pictureId: String) {
        pictureApiService.deletePicture(
            modelType = "disaster_aid",
            modelId = aidId,
            pictureId = pictureId
        )
    }

    fun mapDisasterAidDtoToModel(dto: DisasterAidDto): DisasterAid {
        return DisasterAid(
            id = dto.id ?: "",
            disasterId = dto.disasterId ?: "",
            reportedBy = dto.reportedBy ?: "",
            donator = dto.donator ?: "",
            location = dto.location ?: "",
            title = dto.title ?: "",
            description = dto.description ?: "",
            category = dto.category ?: "",
            quantity = dto.quantity ?: 0,
            unit = dto.unit ?: "",
            createdAt = dto.createdAt ?: "",
            updatedAt = dto.updatedAt ?: "",
            disasterTitle = dto.disaster?.title,
            disasterLocation = dto.disaster?.location,
            reporterName = dto.reporter?.user?.name,
            pictures = dto.pictures?.map { pictureDto ->
                VictimPicture(
                    id = pictureDto.id ?: "",
                    url = pictureDto.url ?: "",
                    caption = pictureDto.caption,
                    mimeType = pictureDto.mimeType ?: "image/jpeg"
                )
            }
        )
    }
}
