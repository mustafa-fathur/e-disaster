package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterVictimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterVictimDao {
    @Query("SELECT * FROM disaster_victims WHERE disaster_id = :disasterId AND sync_status != 'PENDING_DELETE' ORDER BY created_at DESC")
    fun getVictimsByDisasterId(disasterId: String): Flow<List<DisasterVictimEntity>>

    @Query("SELECT * FROM disaster_victims WHERE id = :id AND sync_status != 'PENDING_DELETE'")
    suspend fun getVictimById(id: String): DisasterVictimEntity?

    @Query("SELECT * FROM disaster_victims WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncVictims(): List<DisasterVictimEntity>

    @Query("SELECT * FROM disaster_victims WHERE local_id = :localId")
    suspend fun getVictimByLocalId(localId: String): DisasterVictimEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVictim(victim: DisasterVictimEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVictims(victims: List<DisasterVictimEntity>)

    @Update
    suspend fun updateVictim(victim: DisasterVictimEntity)

    @Query("UPDATE disaster_victims SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM disaster_victims WHERE id = :id")
    suspend fun deleteById(id: String)
}

