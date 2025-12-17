package com.example.e_disaster.data.local.sync

import android.util.Log
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.dao.DisasterDao
import com.example.e_disaster.data.local.database.dao.DisasterVictimDao
import com.example.e_disaster.data.local.database.dao.PictureDao
import com.example.e_disaster.data.local.database.entities.DisasterEntity
import com.example.e_disaster.data.local.database.entities.DisasterVictimEntity
import com.example.e_disaster.data.local.database.entities.PictureEntity
import com.example.e_disaster.data.local.storage.ImageManager
import com.example.e_disaster.data.mapper.DisasterMapper
import com.example.e_disaster.data.mapper.DisasterVictimMapper
import com.example.e_disaster.data.remote.service.DisasterApiService
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SyncManager handles synchronization between local Room database and remote API.
 * This is a base implementation that can be extended for additional features.
 */
@Singleton
class SyncManager @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val disasterDao: DisasterDao,
    private val victimDao: DisasterVictimDao,
    private val pictureDao: PictureDao,
    private val disasterApiService: DisasterApiService,
    private val victimApiService: DisasterVictimApiService,
    private val pictureApiService: PictureApiService,
    private val imageManager: ImageManager
) {

    /**
     * Sync all pending changes (push local changes to server)
     */
    suspend fun syncAll() {
        if (!networkMonitor.isOnline()) {
            Log.d("SyncManager", "Offline - skipping sync")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                syncDisasters()
                syncVictims()
                syncPictures()
            } catch (e: Exception) {
                Log.e("SyncManager", "Error during sync", e)
            }
        }
    }

    /**
     * Sync disasters: Push pending changes, then pull latest from server
     */
    suspend fun syncDisasters() {
        if (!networkMonitor.isOnline()) return

        try {
            // Note: Disaster create/update/delete not in current features
            // This sync only pulls latest disasters from server

            // Pull latest disasters from server
            val response = disasterApiService.getDisasters()
            val entities = response.data.map { dto ->
                with(DisasterMapper) {
                    dto.toEntity(syncStatus = SyncStatus.SYNCED)
                }
            }
            disasterDao.insertDisasters(entities)
            Log.d("SyncManager", "Pulled ${entities.size} disasters from server")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing disasters", e)
        }
    }

    /**
     * Sync victims: Push pending changes, then pull latest from server
     * Uses Last-Write-Wins (LWW) strategy for conflict resolution
     */
    suspend fun syncVictims() {
        if (!networkMonitor.isOnline()) return

        try {
            // Push pending victims
            val pendingVictims = victimDao.getPendingSyncVictims()
            for (victim in pendingVictims) {
                when (victim.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        // Note: Creating victims requires multipart form data, handled in repository
                        // This sync mainly handles updates and deletes
                        Log.d("SyncManager", "Skipping PENDING_CREATE victim sync for ${victim.id} (handled by repo)")
                    }
                    SyncStatus.PENDING_UPDATE -> {
                        try {
                            // Check for conflicts: Get server version to compare timestamps
                            val serverResponse = try {
                                victimApiService.getDisasterVictimDetail(
                                    victim.disasterId ?: "",
                                    victim.id
                                )
                            } catch (e: Exception) {
                                // If victim doesn't exist on server (e.g., was deleted), handle conflict
                                Log.w("SyncManager", "Victim ${victim.id} not found on server (may have been deleted)")
                                // Mark as sync failed - user will need to handle this
                                victimDao.updateSyncStatus(
                                    victim.id,
                                    SyncStatus.SYNC_FAILED,
                                    System.currentTimeMillis()
                                )
                                continue
                            }

                            val serverVictim = serverResponse.data
                            val serverUpdatedAt = serverVictim.updatedAt?.let { parseDateString(it) }
                                ?: System.currentTimeMillis()

                            // Last-Write-Wins: Compare timestamps
                            if (serverUpdatedAt > victim.updatedAt) {
                                // Server has newer version - CONFLICT!
                                Log.w("SyncManager", "Conflict detected for victim ${victim.id}: Server version is newer (server: $serverUpdatedAt, local: ${victim.updatedAt})")
                                // Server wins: Update local with server version
                                val serverEntity = with(DisasterVictimMapper) {
                                    serverVictim.toEntity(syncStatus = SyncStatus.SYNCED)
                                }
                                victimDao.insertVictim(serverEntity)
                                Log.d("SyncManager", "Resolved conflict: Used server version for victim ${victim.id}")
                            } else {
                                // Client version is newer or same - safe to update server
                                val formattedDate = try {
                                    val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    java.time.LocalDate.parse(victim.dateOfBirth ?: "", inputFormatter)
                                        .format(outputFormatter)
                                } catch (e: Exception) {
                                    victim.dateOfBirth ?: ""
                                }

                                val statusApiValue = victim.status ?: "minor_injury"

                                val requestBody = com.example.e_disaster.data.remote.dto.disaster_victim.UpdateVictimRequest(
                                    nik = victim.nik ?: "",
                                    name = victim.name ?: "",
                                    dateOfBirth = formattedDate,
                                    gender = victim.gender == "Perempuan",
                                    status = statusApiValue,
                                    isEvacuated = victim.isEvacuated,
                                    contactInfo = victim.contactInfo,
                                    description = victim.description
                                )

                                val updateResponse = victimApiService.updateDisasterVictim(
                                    victim.disasterId ?: "",
                                    victim.id,
                                    requestBody
                                )

                                // Update local with server response
                                updateResponse.data?.let { victimDto ->
                                    val syncedEntity = with(DisasterVictimMapper) {
                                        victimDto.toEntity(
                                            disasterId = victimDto.disasterId ?: victim.disasterId,
                                            reportedBy = victimDto.reportedBy,
                                            syncStatus = SyncStatus.SYNCED,
                                            lastSyncedAt = System.currentTimeMillis()
                                        )
                                    }
                                    victimDao.insertVictim(syncedEntity)
                                    Log.d("SyncManager", "Updated victim on server: ${victim.id}")
                                } ?: run {
                                    // If no data in response, just mark as synced
                                    victimDao.updateSyncStatus(
                                        victim.id,
                                        SyncStatus.SYNCED,
                                        System.currentTimeMillis()
                                    )
                                    Log.d("SyncManager", "Marked victim as synced: ${victim.id}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SyncManager", "Failed to sync victim update: ${victim.id}", e)
                            // Mark as sync failed
                            victimDao.updateSyncStatus(
                                victim.id,
                                SyncStatus.SYNC_FAILED,
                                System.currentTimeMillis()
                            )
                        }
                    }
                    SyncStatus.PENDING_DELETE -> {
                        try {
                            // Check if victim still exists on server
                            val serverResponse = try {
                                victimApiService.getDisasterVictimDetail(
                                    victim.disasterId ?: "",
                                    victim.id
                                )
                            } catch (e: Exception) {
                                // Victim doesn't exist on server - already deleted or never existed
                                Log.d("SyncManager", "Victim ${victim.id} not found on server, proceeding with local delete")
                                // Hard delete from local DB
                                victimDao.deleteById(victim.id)
                                imageManager.deleteAllImagesForForeignId(victim.id, "victim")
                                Log.d("SyncManager", "Deleted victim locally (was already deleted on server): ${victim.id}")
                                continue
                            }

                            val serverVictim = serverResponse.data
                            val serverUpdatedAt = serverVictim.updatedAt?.let { parseDateString(it) }
                                ?: System.currentTimeMillis()

                            // Conflict check: If server version was updated after local delete timestamp
                            if (serverUpdatedAt > victim.updatedAt) {
                                // Server has newer version - CONFLICT! Someone edited after we deleted
                                Log.w("SyncManager", "Conflict: Victim ${victim.id} was edited on server after local delete")
                                // Cancel delete: Restore with server version
                                val serverEntity = with(DisasterVictimMapper) {
                                    serverVictim.toEntity(syncStatus = SyncStatus.SYNCED)
                                }
                                victimDao.insertVictim(serverEntity)
                                Log.d("SyncManager", "Cancelled delete: Restored victim ${victim.id} with server version")
                            } else {
                                // Safe to delete: Local delete is newer or same
                                victimApiService.deleteDisasterVictim(
                                    victim.disasterId ?: "",
                                    victim.id
                                )
                                // Hard delete from local DB after server confirms
                                victimDao.deleteById(victim.id)
                                imageManager.deleteAllImagesForForeignId(victim.id, "victim")
                                Log.d("SyncManager", "Deleted victim from server and local DB: ${victim.id}")
                            }
                        } catch (e: Exception) {
                            Log.e("SyncManager", "Failed to delete victim: ${victim.id}", e)
                            // Keep as PENDING_DELETE for retry
                        }
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing victims", e)
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

    /**
     * Sync pictures: Upload pending pictures and delete pending deletions
     */
    suspend fun syncPictures() {
        if (!networkMonitor.isOnline()) return

        try {
            val pendingPictures = pictureDao.getPendingSyncPictures()
            for (picture in pendingPictures) {
                when (picture.syncStatus) {
                    SyncStatus.PENDING_CREATE -> {
                        if (picture.localPath != null) {
                            try {
                                // Upload picture
                                val imageFile = File(picture.localPath)
                                if (imageFile.exists()) {
                                    val mimeType = picture.mimeType ?: "image/jpeg"
                                    val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                                    val imagePart = MultipartBody.Part.createFormData(
                                        "image",
                                        imageFile.name,
                                        requestBody
                                    )

                                    val response = pictureApiService.uploadPicture(
                                        modelType = picture.type,
                                        modelId = picture.foreignId,
                                        image = imagePart
                                    )

                                    // Update picture entity - response is Unit, so we mark as synced
                                    // The actual URL will be fetched when the parent entity is synced
                                    if (response.isSuccessful) {
                                        val updated = picture.copy(
                                            syncStatus = SyncStatus.SYNCED,
                                            lastSyncedAt = System.currentTimeMillis()
                                        )
                                        pictureDao.insertPicture(updated)
                                        Log.d("SyncManager", "Uploaded picture: ${picture.id}")
                                    } else {
                                        Log.e("SyncManager", "Failed to upload picture: ${picture.id}, HTTP ${response.code()}")
                                    }
                                } else {
                                    Log.w("SyncManager", "Picture file not found: ${picture.localPath}")
                                }
                            } catch (e: Exception) {
                                Log.e("SyncManager", "Failed to upload picture: ${picture.id}", e)
                            }
                        }
                    }
                    SyncStatus.PENDING_DELETE -> {
                        try {
                            val response = pictureApiService.deletePicture(
                                modelType = picture.type,
                                modelId = picture.foreignId,
                                pictureId = picture.id
                            )
                            
                            if (response.isSuccessful) {
                                // Hard delete from local DB after server confirms
                                pictureDao.deleteById(picture.id)
                                // Also delete local image file
                                picture.localPath?.let { imageManager.deleteImageByPath(it) }
                                Log.d("SyncManager", "Deleted picture from server and local DB: ${picture.id}")
                            } else {
                                Log.e("SyncManager", "Failed to delete picture from server: ${picture.id}, HTTP ${response.code()}")
                                // Keep as PENDING_DELETE for retry
                            }
                        } catch (e: Exception) {
                            // If picture doesn't exist on server (404), it's safe to delete locally
                            if (e.message?.contains("404") == true || e.message?.contains("Not Found") == true) {
                                Log.d("SyncManager", "Picture not found on server, deleting locally: ${picture.id}")
                                pictureDao.deleteById(picture.id)
                                picture.localPath?.let { imageManager.deleteImageByPath(it) }
                            } else {
                                Log.e("SyncManager", "Failed to delete picture from server: ${picture.id}", e)
                                // Keep as PENDING_DELETE for retry
                            }
                        }
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing pictures", e)
        }
    }
}

