package com.example.e_disaster.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.dao.DisasterVictimDao
import com.example.e_disaster.data.local.database.dao.PictureDao
import com.example.e_disaster.data.local.database.entities.DisasterVictimEntity
import com.example.e_disaster.data.local.database.entities.PictureEntity
import com.example.e_disaster.data.local.storage.ImageManager
import com.example.e_disaster.data.local.sync.NetworkMonitor
import com.example.e_disaster.data.mapper.DisasterVictimMapper
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
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterVictimRepository @Inject constructor(
    private val apiService: DisasterVictimApiService,
    private val pictureApiService: PictureApiService,
    private val victimDao: DisasterVictimDao,
    private val networkMonitor: NetworkMonitor,
    private val imageManager: ImageManager,
    private val pictureDao: PictureDao
) {
    suspend fun getDisasterVictims(disasterId: String): List<DisasterVictim> {
        // Try to get from local database first
        val localEntities = victimDao.getVictimsByDisasterId(disasterId).first()
        
        // Deduplicate: Remove victims that have the same localId but different IDs
        // (This happens when a victim is synced - we keep the synced version)
        val deduplicatedEntities = if (localEntities.isNotEmpty()) {
            val seenLocalIds = mutableSetOf<String?>()
            val entitiesToKeep = mutableListOf<DisasterVictimEntity>()
            
            // First pass: Keep SYNCED victims and track their localIds
            localEntities.forEach { entity ->
                if (entity.syncStatus == SyncStatus.SYNCED) {
                    entitiesToKeep.add(entity)
                    entity.localId?.let { seenLocalIds.add(it) }
                }
            }
            
            // Second pass: Keep PENDING victims only if their localId hasn't been seen
            localEntities.forEach { entity ->
                if (entity.syncStatus != SyncStatus.SYNCED) {
                    if (entity.localId == null || entity.localId !in seenLocalIds) {
                        entitiesToKeep.add(entity)
                    } else {
                        // This is a duplicate - delete it
                        Log.d("VictimRepo", "Removing duplicate victim with localId: ${entity.localId}, ID: ${entity.id}")
                        victimDao.deleteById(entity.id)
                    }
                }
            }
            
            entitiesToKeep
        } else {
            localEntities
        }
        
        if (deduplicatedEntities.isNotEmpty()) {
            Log.d("VictimRepo", "Found ${deduplicatedEntities.size} victims in local database for disaster: $disasterId (after deduplication)")
            // Still try to fetch from API in background to update, but return local data immediately
            if (networkMonitor.isOnline()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.getDisasterVictims(disasterId)
                        val entities = response.data.map { dto ->
                            with(DisasterVictimMapper) {
                                dto.toEntity(
                                    disasterId = dto.disasterId ?: disasterId,
                                    reportedBy = dto.reportedBy,
                                    syncStatus = SyncStatus.SYNCED
                                )
                            }
                        }
                        Log.d("VictimRepo", "Attempting to update ${entities.size} victims in local database for disaster: $disasterId")
                        victimDao.insertVictims(entities)
                        Log.d("VictimRepo", "Successfully updated ${entities.size} victims in local database for disaster: $disasterId")
                    } catch (e: Exception) {
                        Log.w("VictimRepo", "Failed to fetch victims from API, using local data: ${e.message}")
                    }
                }
            }
            
            // Get pictures for each victim
            return deduplicatedEntities.map { entity ->
                val pictures = pictureDao.getPicturesByForeignId(entity.id, "victim").first()
                with(DisasterVictimMapper) { 
                    entity.toModel(pictures.map { pictureEntity ->
                        VictimPicture(
                            id = pictureEntity.id,
                            url = pictureEntity.serverUrl ?: "",
                            localPath = pictureEntity.localPath,
                            caption = pictureEntity.caption,
                            mimeType = pictureEntity.mimeType ?: "image/jpeg"
                        )
                    })
                }
            }
        }
        
        // If not in local database and online, fetch from API
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.getDisasterVictims(disasterId)
                val victims = response.data.map { mapVictimDtoToVictim(it, disasterId) }
                
                // Save to local database
                val entities = response.data.map { dto ->
                    with(DisasterVictimMapper) {
                        dto.toEntity(
                            disasterId = dto.disasterId ?: disasterId,
                            reportedBy = dto.reportedBy,
                            syncStatus = SyncStatus.SYNCED
                        )
                    }
                }
                Log.d("VictimRepo", "Attempting to save ${entities.size} victims to local database for disaster: $disasterId")
                victimDao.insertVictims(entities)
                Log.d("VictimRepo", "Successfully saved ${entities.size} victims to local database for disaster: $disasterId")
                
                // Note: Pictures are not included in list response, they're fetched when viewing detail
                
                return victims
            } catch (e: Exception) {
                Log.e("VictimRepo", "Failed to fetch victims from API", e)
                throw Exception("Victim not found locally and unable to fetch from server: ${e.message}")
            }
        } else {
            // Offline and not in local DB
            return emptyList()
        }
    }

    /**
     * Force refresh victims from API and update local database
     */
    suspend fun refreshDisasterVictims(disasterId: String) {
        if (!networkMonitor.isOnline()) {
            Log.d("VictimRepo", "Offline - cannot refresh victims")
            return
        }
        try {
            val response = apiService.getDisasterVictims(disasterId)
            val entities = response.data.map { dto ->
                with(DisasterVictimMapper) {
                    dto.toEntity(
                        disasterId = dto.disasterId ?: disasterId,
                        reportedBy = dto.reportedBy,
                        syncStatus = SyncStatus.SYNCED
                    )
                }
            }
            victimDao.insertVictims(entities)
            Log.d("VictimRepo", "Refreshed ${entities.size} victims from API for disaster: $disasterId")
        } catch (e: Exception) {
            Log.e("VictimRepo", "Failed to refresh victims from API", e)
            throw e
        }
    }

    suspend fun getDisasterVictimDetail(
        disasterId: String,
        victimId: String
    ): DisasterVictim {
        // Try to get from local database first by primary ID
        var localEntity = victimDao.getVictimById(victimId)

        // If not found by ID (e.g. this was a localId that has been replaced by a server ID),
        // try to resolve using local_id column
        if (localEntity == null) {
            localEntity = victimDao.getVictimByLocalId(victimId)
        }

        // Decide which ID to use when loading pictures (prefer actual entity ID)
        val effectiveId = localEntity?.id ?: victimId
        val localPictures = pictureDao.getPicturesByForeignId(effectiveId, "victim").first()

        if (localEntity != null) {
            Log.d(
                "VictimRepo",
                "Found victim detail in local database. requestedId=$victimId, effectiveId=$effectiveId, syncStatus=${localEntity.syncStatus}"
            )
            
            // If victim is pending sync and online, try to sync first (only once)
            if (localEntity.syncStatus == SyncStatus.PENDING_CREATE && networkMonitor.isOnline()) {
                // Check if there's already a synced version of this victim (by localId)
                // This prevents duplicate syncing if the victim was already synced but we're still looking at the old record
                val syncedVersion = localEntity.localId?.let { localId ->
                    victimDao.getVictimByLocalId(localId)?.takeIf { 
                        it.syncStatus == SyncStatus.SYNCED && it.id != victimId 
                    }
                }
                
                if (syncedVersion != null) {
                    // Already synced, use the synced version
                    Log.d("VictimRepo", "Victim already synced, using synced version: ${syncedVersion.id} (was looking for: $victimId)")
                    val updatedPictures = pictureDao.getPicturesByForeignId(syncedVersion.id, "victim").first()
                    return with(DisasterVictimMapper) {
                        syncedVersion.toModel(updatedPictures.map { pictureEntity ->
                            VictimPicture(
                                id = pictureEntity.id,
                                url = pictureEntity.serverUrl ?: "",
                                localPath = pictureEntity.localPath,
                                caption = pictureEntity.caption,
                                mimeType = pictureEntity.mimeType ?: "image/jpeg"
                            )
                        })
                    }
                }
                
                // Not synced yet, attempt to sync
                Log.d("VictimRepo", "Victim is pending sync, attempting to sync: $victimId")
                try {
                    // Try to sync this specific victim
                    val syncedId = syncPendingVictim(localEntity, disasterId)
                    // After sync, get the updated entity (ID might have changed)
                    val syncedEntity = if (syncedId != null && syncedId != victimId) {
                        // ID changed after sync, delete old record and get by new ID
                        victimDao.deleteById(victimId) // Delete old record with localId
                        Log.d("VictimRepo", "Deleted old record with localId: $victimId, new ID: $syncedId")
                        victimDao.getVictimById(syncedId)
                    } else {
                        // ID didn't change, get by original ID
                        victimDao.getVictimById(victimId)
                    }
                    
                    if (syncedEntity != null && syncedEntity.syncStatus == SyncStatus.SYNCED) {
                        // Use the synced entity with updated ID
                        val updatedPictures = pictureDao.getPicturesByForeignId(syncedEntity.id, "victim").first()
                        return with(DisasterVictimMapper) {
                            syncedEntity.toModel(updatedPictures.map { pictureEntity ->
                                VictimPicture(
                                    id = pictureEntity.id,
                                    url = pictureEntity.serverUrl ?: "",
                                    localPath = pictureEntity.localPath,
                                    caption = pictureEntity.caption,
                                    mimeType = pictureEntity.mimeType ?: "image/jpeg"
                                )
                            })
                        }
                    }
                } catch (e: Exception) {
                    Log.w("VictimRepo", "Failed to sync pending victim, using local data: ${e.message}")
                }
            }
            
            // Return local data immediately, but try to update from API in background if online
            if (networkMonitor.isOnline() && localEntity.syncStatus == SyncStatus.SYNCED) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.getDisasterVictimDetail(disasterId, localEntity.id)
                        val entity = with(DisasterVictimMapper) {
                            response.data.toEntity(syncStatus = SyncStatus.SYNCED)
                        }
                        victimDao.insertVictim(entity)
                        Log.d("VictimRepo", "Updated victim detail in local database from API: $victimId")

                        // Update pictures
                        val remotePictures = response.data.pictures?.map { pictureDto ->
                            PictureEntity(
                                id = pictureDto.id ?: UUID.randomUUID().toString(),
                                foreignId = localEntity.id,
                                type = "victim",
                                caption = pictureDto.caption,
                                localPath = null,
                                serverUrl = pictureDto.url,
                                filePath = pictureDto.filePath,
                                mimeType = pictureDto.mimeType,
                                altText = null,
                                syncStatus = SyncStatus.SYNCED,
                                localId = null,
                                createdAt = System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis(),
                                lastSyncedAt = System.currentTimeMillis()
                            )
                        } ?: emptyList()
                        pictureDao.deleteByForeignId(localEntity.id, "victim") // Clear old pictures
                        pictureDao.insertPictures(remotePictures)
                        Log.d("VictimRepo", "Updated pictures for victim: $victimId")
                    } catch (e: Exception) {
                        Log.w("VictimRepo", "Failed to fetch victim detail from API, using local data: ${e.message}")
                    }
                }
            }
            
            return with(DisasterVictimMapper) { 
                localEntity.toModel(localPictures.map { pictureEntity ->
                    VictimPicture(
                        id = pictureEntity.id,
                        url = pictureEntity.serverUrl ?: "",
                        localPath = pictureEntity.localPath,
                        caption = pictureEntity.caption,
                        mimeType = pictureEntity.mimeType ?: "image/jpeg"
                    )
                })
            }
        }

        // If not in local database and online, fetch from API
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.getDisasterVictimDetail(disasterId, victimId)
                val victim = mapVictimDetailDtoToVictim(response.data)

                // Save to local database
                val entity = with(DisasterVictimMapper) {
                    response.data.toEntity(syncStatus = SyncStatus.SYNCED)
                }
                victimDao.insertVictim(entity)
                Log.d("VictimRepo", "Saved victim detail to local database: $victimId")

                val pictures = response.data.pictures?.map { pictureDto ->
                    PictureEntity(
                        id = pictureDto.id ?: UUID.randomUUID().toString(),
                        foreignId = victimId,
                        type = "victim",
                        caption = pictureDto.caption,
                        localPath = null,
                        serverUrl = pictureDto.url,
                        filePath = pictureDto.filePath,
                        mimeType = pictureDto.mimeType,
                        altText = null,
                        syncStatus = SyncStatus.SYNCED,
                        localId = null,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                        lastSyncedAt = System.currentTimeMillis()
                    )
                } ?: emptyList()
                pictureDao.insertPictures(pictures)
                Log.d("VictimRepo", "Saved ${pictures.size} pictures for victim: $victimId")

                return victim
            } catch (e: Exception) {
                Log.e("VictimRepo", "Failed to fetch victim detail from API", e)
                throw Exception("Victim not found in local database. Please connect to internet to load victim details.")
            }
        } else {
            // Offline and not in local DB
            throw Exception("Victim not found in local database. Please connect to internet to load victim details.")
        }
    }
    
    /**
     * Sync a pending victim to the server
     * @return The new server ID if sync was successful, null otherwise
     */
    private suspend fun syncPendingVictim(victimEntity: DisasterVictimEntity, disasterId: String): String? {
        if (!networkMonitor.isOnline()) return null
        
        try {
            val nikPart = victimEntity.nik?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = victimEntity.name?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
            val dobPart = victimEntity.dateOfBirth?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
            
            val genderValue = if (victimEntity.gender == "Perempuan") "1" else "0"
            val genderPart = genderValue.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val statusApiValue = when (victimEntity.status) {
                "minor_injury" -> "minor_injury"
                "serious_injuries" -> "serious_injuries"
                "deceased" -> "deceased"
                "lost" -> "lost"
                else -> "minor_injury"
            }
            val statusPart = statusApiValue.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val isEvacuatedValue = if (victimEntity.isEvacuated) "1" else "0"
            val isEvacuatedPart = isEvacuatedValue.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val contactPart = victimEntity.contactInfo?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = victimEntity.description?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            // Get pictures for this victim
            val localPictures = pictureDao.getPicturesByForeignId(victimEntity.id, "victim").first()
            val imageParts = localPictures.mapNotNull { picture ->
                picture.localPath?.let { path ->
                    val file = File(path)
                    if (!file.exists()) return@mapNotNull null
                    val mimeType = picture.mimeType ?: "image/jpeg"
                    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images[]", file.name, requestBody)
                }
            }
            
            val url = "disasters/$disasterId/victims"
            val response = apiService.addDisasterVictim(
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
            
            // Update local database with server ID and SYNCED status
            return response.data?.let { victimDto ->
                val serverId = victimDto.id ?: victimEntity.id
                
                // Check if we already have a synced version of this victim (by localId)
                val existingSynced = victimEntity.localId?.let { localId ->
                    victimDao.getVictimByLocalId(localId)?.takeIf { it.syncStatus == SyncStatus.SYNCED }
                }
                
                if (existingSynced != null && existingSynced.id == serverId) {
                    // Already synced, just return the existing ID
                    Log.d("VictimRepo", "Victim already synced: $serverId (localId: ${victimEntity.localId})")
                    return@let serverId
                }
                
                // Delete old record if ID changed
                if (victimEntity.id != serverId) {
                    victimDao.deleteById(victimEntity.id)
                    Log.d("VictimRepo", "Deleted old record with localId: ${victimEntity.id}")
                }
                
                val updatedEntity = victimEntity.copy(
                    id = serverId,
                    disasterId = victimDto.disasterId ?: disasterId,
                    reportedBy = victimDto.reportedBy,
                    reporterName = victimDto.reporter?.user?.name,
                    syncStatus = SyncStatus.SYNCED,
                    lastSyncedAt = System.currentTimeMillis()
                )
                victimDao.insertVictim(updatedEntity)
                
                // Update picture entities with server victim ID
                // Note: DisasterVictimDto doesn't include pictures, so we update foreignId and mark as synced
                // The actual picture URLs will be fetched when detail is loaded
                localPictures.forEach { localPicture ->
                    val updated = localPicture.copy(
                        foreignId = serverId, // Use server ID
                        syncStatus = SyncStatus.SYNCED,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                    pictureDao.insertPicture(updated)
                }
                Log.d("VictimRepo", "Synced pending victim to server: $serverId (was: ${victimEntity.id}, localId: ${victimEntity.localId})")
                serverId
            }
        } catch (e: Exception) {
            Log.e("VictimRepo", "Failed to sync pending victim: ${victimEntity.id}", e)
            throw e
        }
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
                    localPath = null, // Remote pictures don't have local path
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
        val now = System.currentTimeMillis()
        val localId = "local_${now}_${uiState.nik}"
        
        // 1. Save images locally first
        val savedImagePaths = mutableListOf<String>()
        uiState.images.forEachIndexed { index, uri ->
            val imageId = "${localId}_img_$index"
            val localPath = imageManager.saveImage(uri, imageId)
            localPath?.let { savedImagePaths.add(it) }
            
            // Save picture entity for sync
            if (localPath != null) {
                val pictureEntity = PictureEntity(
                    id = imageId,
                    foreignId = localId, // Will be updated with server victim ID later
                    type = "victim",
                    caption = null,
                    localPath = localPath,
                    serverUrl = null,
                    filePath = null,
                    mimeType = context.contentResolver.getType(uri) ?: "image/jpeg",
                    altText = null,
                    syncStatus = SyncStatus.PENDING_CREATE,
                    localId = imageId,
                    createdAt = now,
                    updatedAt = now,
                    lastSyncedAt = null
                )
                pictureDao.insertPicture(pictureEntity)
            }
        }
        
        // 2. Create victim entity locally with PENDING_CREATE status
        val genderBoolean = uiState.gender == "Perempuan"
        val statusApiValue = when (uiState.victimStatus) {
            "Luka Ringan" -> "minor_injury"
            "Luka Berat" -> "serious_injuries"
            "Meninggal" -> "deceased"
            "Hilang" -> "lost"
            else -> "minor_injury"
        }
        
        val formattedDob = if (uiState.dob.isNotBlank()) {
            try {
                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                LocalDate.parse(uiState.dob, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                ""
            }
        } else ""
        
        val victimEntity = DisasterVictimEntity(
            id = localId,
            disasterId = disasterId,
            reportedBy = null, // Will be set from server response
            nik = uiState.nik,
            name = uiState.name,
            dateOfBirth = formattedDob,
            gender = uiState.gender,
            contactInfo = uiState.contact.ifBlank { null },
            description = uiState.description.ifBlank { null },
            isEvacuated = uiState.isEvacuated,
            status = statusApiValue,
            reporterName = null,
            syncStatus = if (networkMonitor.isOnline()) SyncStatus.PENDING_CREATE else SyncStatus.PENDING_CREATE,
            localId = localId,
            createdAt = now,
            updatedAt = now,
            lastSyncedAt = null
        )
        victimDao.insertVictim(victimEntity)
        Log.d("VictimRepo", "Saved victim locally with ID: $localId")
        
        // 3. Try to upload to server if online
        if (networkMonitor.isOnline()) {
            try {
                val nikPart = uiState.nik.toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = uiState.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val dobPart = formattedDob.toRequestBody("text/plain".toMediaTypeOrNull())
                val genderValue = if (uiState.gender == "Perempuan") "1" else "0"
                val genderPart = genderValue.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusPart = statusApiValue.toRequestBody("text/plain".toMediaTypeOrNull())
                val isEvacuatedValue = if (uiState.isEvacuated) "1" else "0"
                val isEvacuatedPart = isEvacuatedValue.toRequestBody("text/plain".toMediaTypeOrNull())
                val contactPart = uiState.contact.ifBlank { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = uiState.description.ifBlank { null }?.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageParts = savedImagePaths.mapNotNull { localPath ->
                    val file = File(localPath)
                    if (file.exists()) {
                        val mimeType = "image/jpeg"
                        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images[]", file.name, requestBody)
                    } else null
                }

                val url = "disasters/$disasterId/victims"
                val response = apiService.addDisasterVictim(
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
                
                // Update local entity with server response
                response.data?.let { victimDto ->
                    val serverId = victimDto.id ?: localId
                    
                    // Delete old localId record if ID changed (to prevent duplicates)
                    if (serverId != localId) {
                        victimDao.deleteById(localId)
                        Log.d("VictimRepo", "Deleted old localId record: $localId (replaced with server ID: $serverId)")
                    }
                    
                    val updatedEntity = with(DisasterVictimMapper) {
                        victimDto.toEntity(
                            disasterId = victimDto.disasterId ?: disasterId,
                            reportedBy = victimDto.reportedBy,
                            syncStatus = SyncStatus.SYNCED
                        )
                    }
                    victimDao.insertVictim(updatedEntity)
                    
                    // Update picture entities with server victim ID
                    savedImagePaths.forEachIndexed { index, _ ->
                        val imageId = "${localId}_img_$index"
                        val picture = pictureDao.getPictureByLocalId(imageId)
                        picture?.let {
                            val updated = it.copy(
                                foreignId = updatedEntity.id, // Use server ID
                                syncStatus = SyncStatus.SYNCED,
                                lastSyncedAt = System.currentTimeMillis()
                            )
                            pictureDao.insertPicture(updated)
                        }
                    }
                    
                    Log.d("VictimRepo", "Synced victim to server: ${updatedEntity.id} (was: $localId)")
                }
                
                return response
            } catch (e: Exception) {
                Log.e("VictimRepo", "Failed to upload victim to server, will sync later", e)
                // Return a mock response for offline mode
                return AddVictimResponse(
                    message = "Victim saved locally. Will sync when online.",
                    data = null
                )
            }
        } else {
            // Offline - return mock response
            Log.d("VictimRepo", "Offline - victim saved locally, will sync when online")
            return AddVictimResponse(
                message = "Victim saved locally. Will sync when online.",
                data = null
            )
        }
    }

    suspend fun updateDisasterVictim(
        disasterId: String,
        victimId: String,
        uiState: UpdateVictimUiState
    ): AddVictimResponse {
        val now = System.currentTimeMillis()
        
        // 1. Get existing victim from local database - try by ID first, then by localId
        var existingVictim = victimDao.getVictimById(victimId)
        if (existingVictim == null) {
            existingVictim = victimDao.getVictimByLocalId(victimId)
        }
        existingVictim ?: throw Exception("Victim not found locally for update")
        
        // Use actual ID from entity
        val actualVictimId = existingVictim.id
        
        // 2. Update local database immediately with PENDING_UPDATE status
        val updatedVictimEntity = existingVictim.copy(
            nik = uiState.nik,
            name = uiState.name,
            dateOfBirth = uiState.dob,
            gender = uiState.gender,
            contactInfo = uiState.contact,
            description = uiState.description,
            isEvacuated = uiState.isEvacuated,
            status = when (uiState.victimStatus) {
                "Luka Ringan" -> "minor_injury"
                "Luka Berat" -> "serious_injuries"
                "Meninggal" -> "deceased"
                "Hilang" -> "lost"
                else -> uiState.victimStatus
            },
            syncStatus = SyncStatus.PENDING_UPDATE,
            updatedAt = now
        )
        victimDao.insertVictim(updatedVictimEntity)
        Log.d("VictimRepo", "Updated victim locally with PENDING_UPDATE: $actualVictimId")
        
        // 3. Attempt to upload to server if online
        if (networkMonitor.isOnline()) {
            try {
                // 3a. Check for conflicts first (Last-Write-Wins)
                val serverResponse = try {
                    apiService.getDisasterVictimDetail(disasterId, actualVictimId)
                } catch (e: Exception) {
                    // Victim doesn't exist on server (might have been deleted)
                    Log.w("VictimRepo", "Victim $actualVictimId not found on server during update")
                    null
                }
                
                if (serverResponse != null) {
                    val serverVictim = serverResponse.data
                    val serverUpdatedAt = serverVictim.updatedAt?.let { parseDateString(it) } ?: 0L
                    
                    if (serverUpdatedAt > existingVictim.updatedAt) {
                        // Server version is newer - CONFLICT! Server wins
                        Log.w("VictimRepo", "Conflict detected for victim $actualVictimId: Server version is newer (server: $serverUpdatedAt, local: ${existingVictim.updatedAt})")
                        // Update local with server version
                        val serverEntity = with(DisasterVictimMapper) {
                            serverVictim.toEntity(syncStatus = SyncStatus.SYNCED)
                        }
                        victimDao.insertVictim(serverEntity)
                        throw Exception("Data telah diubah oleh pengguna lain. Perubahan Anda tidak disimpan. Data terbaru telah dimuat.")
                    }
                }
                
                // 3b. Client version is newer or same - proceed with update
                // Format date: handle both "dd/MM/yyyy" (from UI) and "yyyy-MM-dd" (from entity)
                val formattedDate = try {
                    // Try parsing as "dd/MM/yyyy" first (UI format)
                    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    LocalDate.parse(uiState.dob, inputFormatter).format(outputFormatter)
                } catch (e: Exception) {
                    // If that fails, check if it's already in "yyyy-MM-dd" format
                    try {
                        LocalDate.parse(uiState.dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        uiState.dob // Already in correct format
                    } catch (e2: Exception) {
                        // If both fail, use as-is (might be empty or invalid)
                        uiState.dob
                    }
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

                val response = apiService.updateDisasterVictim(disasterId, actualVictimId, requestBody)
                
                // Update local database with SYNCED status
                response.data?.let { victimDto ->
                    val syncedEntity = with(DisasterVictimMapper) {
                        victimDto.toEntity(
                            disasterId = victimDto.disasterId ?: disasterId,
                            reportedBy = victimDto.reportedBy,
                            syncStatus = SyncStatus.SYNCED,
                            lastSyncedAt = System.currentTimeMillis()
                        )
                    }
                    victimDao.insertVictim(syncedEntity)
                    Log.d("VictimRepo", "Synced updated victim to server: $actualVictimId")
                }
                
                return response
            } catch (e: Exception) {
                // If it's a conflict exception, re-throw it
                if (e.message?.contains("Data telah diubah") == true) {
                    throw e
                }
                Log.e("VictimRepo", "Failed to upload updated victim to server, will sync later", e)
                return AddVictimResponse(
                    message = "Perubahan disimpan lokal. Akan disinkronkan saat online.",
                    data = null
                )
            }
        } else {
            // Offline - return mock response
            Log.d("VictimRepo", "Offline - victim updated locally, will sync when online")
            return AddVictimResponse(
                message = "Perubahan disimpan lokal. Akan disinkronkan saat online.",
                data = null
            )
        }
    }
    
    /**
     * Parse date string to timestamp (Long)
     * Handles various date formats
     */
    private fun parseDateString(dateString: String): Long {
        return try {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )
            for (format in formats) {
                try {
                    java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                        .parse(dateString)?.time ?: continue
                } catch (e: Exception) {
                    continue
                }
            }
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    suspend fun deleteDisasterVictim(disasterId: String, victimId: String) {
        val now = System.currentTimeMillis()
        
        // 1. Get existing victim from local database - try by ID first, then by localId
        var existingVictim = victimDao.getVictimById(victimId)
        if (existingVictim == null) {
            // Try to find by localId (in case victimId is actually a localId)
            existingVictim = victimDao.getVictimByLocalId(victimId)
        }
        
        if (existingVictim == null) {
            throw Exception("Victim not found locally for deletion (ID: $victimId)")
        }
        
        // Use the actual ID from the entity (might be different from victimId parameter)
        val actualVictimId = existingVictim.id
        
        // 2. Soft delete: Mark as PENDING_DELETE (keep in DB for conflict detection)
        val deletedVictimEntity = existingVictim.copy(
            syncStatus = SyncStatus.PENDING_DELETE,
            updatedAt = now
        )
        victimDao.insertVictim(deletedVictimEntity)
        Log.d("VictimRepo", "Marked victim for deletion locally (soft delete): $actualVictimId")
        
        // 3. Attempt to delete from server if online
        if (networkMonitor.isOnline()) {
            try {
                // Use actualVictimId for server deletion (not the parameter, which might be localId)
                apiService.deleteDisasterVictim(disasterId, actualVictimId)
                // Only hard delete after server confirms deletion
                victimDao.deleteById(actualVictimId)
                // Also delete associated images
                imageManager.deleteAllImagesForForeignId(actualVictimId, "victim")
                Log.d("VictimRepo", "Deleted victim from server and local database: $actualVictimId")
            } catch (e: Exception) {
                // If 404 (not found), it's safe to delete locally
                if (e.message?.contains("404") == true || e.message?.contains("Not Found") == true) {
                    Log.d("VictimRepo", "Victim not found on server (404), deleting locally: $actualVictimId")
                    victimDao.deleteById(actualVictimId)
                    imageManager.deleteAllImagesForForeignId(actualVictimId, "victim")
                } else {
                    Log.e("VictimRepo", "Failed to delete victim from server, will sync later", e)
                    // Keep local record marked as PENDING_DELETE for later sync
                }
            }
        } else {
            // Offline - keep as PENDING_DELETE, will sync when online
            Log.d("VictimRepo", "Offline - victim marked for deletion locally, will sync when online")
        }
    }

    suspend fun addVictimPicture(victimId: String, imageUri: Uri, context: Context) {
        val now = System.currentTimeMillis()
        val localPictureId = UUID.randomUUID().toString()
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
        
        // 1. Save image locally first
        val imageId = "${victimId}_img_${now}"
        val localPath = imageManager.saveImage(imageUri, imageId)
        if (localPath == null) {
            throw Exception("Gagal menyimpan gambar lokal")
        }
        Log.d("VictimRepo", "Saved image locally: $localPath")
        
        // 2. Create PictureEntity with PENDING_CREATE status
        val pictureEntity = PictureEntity(
            id = localPictureId,
            foreignId = victimId,
            type = "victim",
            caption = null,
            localPath = localPath,
            serverUrl = null,
            filePath = null,
            mimeType = mimeType,
            altText = null,
            syncStatus = SyncStatus.PENDING_CREATE,
            localId = localPictureId,
            createdAt = now,
            updatedAt = now,
            lastSyncedAt = null
        )
        pictureDao.insertPicture(pictureEntity)
        Log.d("VictimRepo", "Saved picture entity locally with PENDING_CREATE: $localPictureId")
        
        // 3. Attempt to upload to server if online
        if (networkMonitor.isOnline()) {
            try {
                val file = File(localPath)
                if (!file.exists()) {
                    throw Exception("File gambar tidak ditemukan")
                }
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

                val response = pictureApiService.uploadPicture(
                    modelType = "disaster_victim",
                    modelId = victimId,
                    image = imagePart
                )
                
                if (response.isSuccessful) {
                    // Update PictureEntity with SYNCED status
                    // Note: The API response doesn't include picture data, so we just mark as synced
                    // The actual URL will be fetched when the victim detail is loaded
                    val updated = pictureEntity.copy(
                        syncStatus = SyncStatus.SYNCED,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                    pictureDao.insertPicture(updated)
                    Log.d("VictimRepo", "Uploaded picture to server and updated local DB: $localPictureId")
                } else {
                    Log.w("VictimRepo", "Failed to upload picture to server, HTTP ${response.code()}")
                    // Keep as PENDING_CREATE for later sync
                }
            } catch (e: Exception) {
                Log.e("VictimRepo", "Failed to upload picture to server, will sync later", e)
                // Keep local record as PENDING_CREATE for later sync
            }
        } else {
            Log.d("VictimRepo", "Offline - picture saved locally, will sync when online")
        }
    }

    suspend fun deleteVictimPicture(victimId: String, pictureId: String) {
        val now = System.currentTimeMillis()
        
        // 1. Get existing picture from local database
        val existingPicture = pictureDao.getPictureById(pictureId)
            ?: throw Exception("Gambar tidak ditemukan lokal untuk penghapusan")
        
        // 2. Soft delete: Mark as PENDING_DELETE (keep in DB for conflict detection)
        val deletedPictureEntity = existingPicture.copy(
            syncStatus = SyncStatus.PENDING_DELETE,
            updatedAt = now
        )
        pictureDao.insertPicture(deletedPictureEntity)
        Log.d("VictimRepo", "Marked picture for deletion locally (soft delete): $pictureId")
        
        // 3. Attempt to delete from server if online
        if (networkMonitor.isOnline()) {
            try {
                val response = pictureApiService.deletePicture(
                    modelType = "disaster_victim",
                    modelId = victimId,
                    pictureId = pictureId
                )
                
                if (response.isSuccessful) {
                    // Only hard delete after server confirms deletion
                    pictureDao.deleteById(pictureId)
                    // Also delete local image file
                    existingPicture.localPath?.let { imageManager.deleteImageByPath(it) }
                    Log.d("VictimRepo", "Deleted picture from server and local database: $pictureId")
                } else {
                    Log.w("VictimRepo", "Failed to delete picture from server, HTTP ${response.code()}")
                    // Keep local record marked as PENDING_DELETE for later sync
                }
            } catch (e: Exception) {
                Log.e("VictimRepo", "Failed to delete picture from server, will sync later", e)
                // Keep local record marked as PENDING_DELETE for later sync
            }
        } else {
            // Offline - keep as PENDING_DELETE, will sync when online
            Log.d("VictimRepo", "Offline - picture marked for deletion locally, will sync when online")
        }
    }
}
