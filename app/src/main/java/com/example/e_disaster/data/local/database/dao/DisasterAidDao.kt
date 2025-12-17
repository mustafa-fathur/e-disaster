package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterAidEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterAidDao {
    @Query("SELECT * FROM disaster_aids WHERE disaster_id = :disasterId ORDER BY created_at DESC")
    fun getAidsByDisasterId(disasterId: String): Flow<List<DisasterAidEntity>>

    @Query("SELECT * FROM disaster_aids WHERE id = :id")
    suspend fun getAidById(id: String): DisasterAidEntity?

    @Query("SELECT * FROM disaster_aids WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncAids(): List<DisasterAidEntity>

    @Query("SELECT * FROM disaster_aids WHERE local_id = :localId")
    suspend fun getAidByLocalId(localId: String): DisasterAidEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAid(aid: DisasterAidEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAids(aids: List<DisasterAidEntity>)

    @Update
    suspend fun updateAid(aid: DisasterAidEntity)

    @Query("UPDATE disaster_aids SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM disaster_aids WHERE id = :id")
    suspend fun deleteById(id: String)
}

