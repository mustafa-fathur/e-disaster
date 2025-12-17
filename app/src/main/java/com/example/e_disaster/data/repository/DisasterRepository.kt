package com.example.e_disaster.data.repository

import android.util.Log
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.dao.DisasterDao
import com.example.e_disaster.data.mapper.DisasterMapper
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.local.database.dao.DisasterVolunteerDao
import com.example.e_disaster.data.local.database.dao.UserDao
import com.example.e_disaster.data.local.sync.NetworkMonitor
import com.example.e_disaster.data.local.sync.SyncManager
import com.example.e_disaster.data.remote.service.DisasterApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterRepository @Inject constructor(
    private val apiService: DisasterApiService,
    private val disasterDao: DisasterDao,
    private val volunteerDao: DisasterVolunteerDao,
    private val userDao: UserDao,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) {
    suspend fun getDisasters(): List<Disaster> {
        // Always return from local database first (offline-first)
        val localEntities = disasterDao.getAllDisasters().first()
        if (localEntities.isNotEmpty()) {
            Log.d("DisasterRepo", "Returning ${localEntities.size} disasters from local database")
            // Sync in background if online
            if (networkMonitor.isOnline()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        syncManager.syncDisasters()
                    } catch (e: Exception) {
                        Log.e("DisasterRepo", "Background sync failed", e)
                    }
                }
            }
            return localEntities.map { with(DisasterMapper) { it.toModel() } }
        }
        
        // If no local data and online, fetch from API
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.getDisasters()
                val entities = response.data.map { dto ->
                    with(DisasterMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
                }
                disasterDao.insertDisasters(entities)
                Log.d("DisasterRepo", "Fetched and saved ${entities.size} disasters from API")
                return entities.map { with(DisasterMapper) { it.toModel() } }
            } catch (e: Exception) {
                Log.e("DisasterRepo", "Failed to fetch disasters from API", e)
            }
        }
        
        // Return empty list if offline and no local data
        return emptyList()
    }

    suspend fun getDisasterById(disasterId: String): Disaster {
        // Try to get from local database first
        val localEntity = disasterDao.getDisasterById(disasterId)
        if (localEntity != null) {
            Log.d("DisasterRepo", "Found disaster in local database: $disasterId")
            // Still try to fetch from API in background to update, but return local data immediately
            if (networkMonitor.isOnline()) {
                try {
                    val dto = apiService.getDisasterById(disasterId)
                    val entity = with(DisasterMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
                    disasterDao.insertDisaster(entity)
                    Log.d("DisasterRepo", "Updated disaster in local database: $disasterId")
                } catch (e: Exception) {
                    Log.w("DisasterRepo", "Failed to fetch disaster from API, using local data: ${e.message}")
                }
            }
            return with(DisasterMapper) { localEntity.toModel() }
        }
        
        // If not in local database and online, fetch from API
        if (networkMonitor.isOnline()) {
            try {
                val dto = apiService.getDisasterById(disasterId)
                val disaster = mapDisasterDtoToDisaster(dto)
                
                // Save to local database
                val entity = with(DisasterMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
                disasterDao.insertDisaster(entity)
                Log.d("DisasterRepo", "Saved disaster to local database: $disasterId")
                
                return disaster
            } catch (e: Exception) {
                Log.e("DisasterRepo", "Failed to fetch disaster from API", e)
                throw Exception("Disaster not found locally and unable to fetch from server: ${e.message}")
            }
        } else {
            // Offline and not in local DB
            throw Exception("Disaster not found in local database. Please connect to internet to load disaster details.")
        }
    }

    suspend fun joinDisaster(disasterId: String): String {
        val currentUser = userDao.getCurrentUser()
        if (currentUser == null) {
            throw Exception("User not found. Please login again.")
        }
        
        // Save assignment locally first
        val now = System.currentTimeMillis()
        val localId = "local_${now}_${disasterId}_${currentUser.id}"
        val assignmentEntity = com.example.e_disaster.data.local.database.entities.DisasterVolunteerEntity(
            id = localId,
            disasterId = disasterId,
            userId = currentUser.id,
            syncStatus = if (networkMonitor.isOnline()) SyncStatus.PENDING_CREATE else SyncStatus.PENDING_CREATE,
            localId = localId,
            createdAt = now,
            updatedAt = now,
            lastSyncedAt = null
        )
        volunteerDao.insertVolunteer(assignmentEntity)
        Log.d("DisasterRepo", "Saved assignment locally: $localId")
        
        // Try to join on server if online
        if (networkMonitor.isOnline()) {
            try {
                val response = apiService.joinDisaster(disasterId)
                // Update with server response if available
                // For now, just mark as synced
                volunteerDao.updateSyncStatus(localId, SyncStatus.SYNCED, System.currentTimeMillis())
                return response.message
            } catch (e: Exception) {
                Log.e("DisasterRepo", "Failed to join disaster on server, will sync later", e)
                return "Assignment saved locally. Will sync when online."
            }
        } else {
            return "Assignment saved locally. Will sync when online."
        }
    }

    suspend fun isUserAssigned(disasterId: String): Boolean {
        // Get current user ID from local database
        val currentUser = userDao.getCurrentUser()
        if (currentUser == null) {
            Log.w("DisasterRepo", "No current user found in local database")
            // If offline, return false (user can't be assigned if we don't know who they are)
            if (!networkMonitor.isOnline()) {
                return false
            }
            // If online, try API (but can't save locally without user ID)
            return try {
                apiService.checkAssignment(disasterId).assigned
            } catch (e: Exception) {
                Log.e("DisasterRepo", "Failed to check assignment from API", e)
                false
            }
        }
        
        // Check local database first (offline-first)
        val localAssignment = volunteerDao.isUserAssigned(disasterId, currentUser.id)
        if (localAssignment != null) {
            Log.d("DisasterRepo", "Found assignment in local database for disaster: $disasterId, syncStatus: ${localAssignment.syncStatus}")
            // If PENDING_CREATE and online, try to verify with server
            if (localAssignment.syncStatus == SyncStatus.PENDING_CREATE && networkMonitor.isOnline()) {
                try {
                    val apiResponse = apiService.checkAssignment(disasterId)
                    if (apiResponse.assigned) {
                        // Update local record to SYNCED
                        volunteerDao.updateSyncStatus(localAssignment.id, SyncStatus.SYNCED, System.currentTimeMillis())
                        Log.d("DisasterRepo", "Verified assignment with server, marked as SYNCED")
                    } else {
                        // Server says not assigned, but keep local for now (might be race condition)
                        Log.w("DisasterRepo", "Server says not assigned but local PENDING_CREATE exists - keeping local")
                    }
                } catch (e: Exception) {
                    Log.w("DisasterRepo", "Failed to verify assignment with server, using local data: ${e.message}")
                }
            }
            // Always return true if local assignment exists (offline-first)
            return true
        }
        
        // Not in local DB - check API if online
        if (networkMonitor.isOnline()) {
            try {
                val apiResponse = apiService.checkAssignment(disasterId)
                // If assigned, save to local DB for future offline access
                if (apiResponse.assigned) {
                    val now = System.currentTimeMillis()
                    val assignmentEntity = com.example.e_disaster.data.local.database.entities.DisasterVolunteerEntity(
                        id = "${disasterId}_${currentUser.id}",
                        disasterId = disasterId,
                        userId = currentUser.id,
                        syncStatus = SyncStatus.SYNCED,
                        localId = null,
                        createdAt = now,
                        updatedAt = now,
                        lastSyncedAt = now
                    )
                    volunteerDao.insertVolunteer(assignmentEntity)
                    Log.d("DisasterRepo", "Saved assignment to local database from API")
                }
                return apiResponse.assigned
            } catch (e: Exception) {
                Log.e("DisasterRepo", "Failed to check assignment from API", e)
                return false
            }
        } else {
            // Offline and not in local DB - assume not assigned
            Log.d("DisasterRepo", "Offline and no local assignment found - returning false")
            return false
        }
    }

    /**
     * Force refresh disasters from API and update local database
     */
    suspend fun refreshDisasters() {
        if (!networkMonitor.isOnline()) {
            Log.d("DisasterRepo", "Offline - cannot refresh disasters")
            return
        }
        try {
            val response = apiService.getDisasters()
            val entities = response.data.map { dto ->
                with(DisasterMapper) { dto.toEntity(syncStatus = SyncStatus.SYNCED) }
            }
            disasterDao.insertDisasters(entities)
            Log.d("DisasterRepo", "Refreshed ${entities.size} disasters from API")
        } catch (e: Exception) {
            Log.e("DisasterRepo", "Failed to refresh disasters from API", e)
            throw e
        }
    }

    private fun mapDisasterDtoToDisaster(dto: DisasterDto): Disaster {
        return with(DisasterMapper) { dto.toModel() }
    }
}
