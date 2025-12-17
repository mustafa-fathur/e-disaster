package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterDao {
    @Query("SELECT * FROM disasters ORDER BY updated_at DESC")
    fun getAllDisasters(): Flow<List<DisasterEntity>>

    @Query("SELECT * FROM disasters WHERE id = :id")
    suspend fun getDisasterById(id: String): DisasterEntity?

    @Query("SELECT * FROM disasters WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncDisasters(): List<DisasterEntity>

    @Query("SELECT * FROM disasters WHERE local_id = :localId")
    suspend fun getDisasterByLocalId(localId: String): DisasterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisaster(disaster: DisasterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisasters(disasters: List<DisasterEntity>)

    @Update
    suspend fun updateDisaster(disaster: DisasterEntity)

    @Query("UPDATE disasters SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM disasters WHERE id = :id")
    suspend fun deleteById(id: String)
}