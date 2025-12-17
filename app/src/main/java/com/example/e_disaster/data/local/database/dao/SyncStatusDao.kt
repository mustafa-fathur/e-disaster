package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncOperation
import com.example.e_disaster.data.local.database.entities.SyncStatusEntity

@Dao
interface SyncStatusDao {
    @Query("SELECT * FROM sync_status ORDER BY created_at ASC")
    suspend fun getPendingOperations(): List<SyncStatusEntity>

    @Query("SELECT * FROM sync_status WHERE entity_type = :entityType AND entity_id = :entityId")
    suspend fun getSyncStatus(entityType: String, entityId: String): SyncStatusEntity?

    @Query("SELECT * FROM sync_status WHERE local_id = :localId")
    suspend fun getSyncStatusByLocalId(localId: String): SyncStatusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncStatus(syncStatus: SyncStatusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncStatuses(syncStatuses: List<SyncStatusEntity>)

    @Update
    suspend fun updateSyncStatus(syncStatus: SyncStatusEntity)

    @Query("UPDATE sync_status SET retry_count = retry_count + 1, error_message = :errorMessage WHERE id = :id")
    suspend fun incrementRetryCount(id: Long, errorMessage: String?)

    @Query("DELETE FROM sync_status WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sync_status WHERE entity_type = :entityType AND entity_id = :entityId")
    suspend fun deleteByEntity(entityType: String, entityId: String)
}

