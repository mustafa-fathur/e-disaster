package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.PictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {
    @Query("SELECT * FROM pictures WHERE foreign_id = :foreignId AND type = :type AND sync_status != 'PENDING_DELETE' ORDER BY created_at DESC")
    fun getPicturesByForeignId(foreignId: String, type: String): Flow<List<PictureEntity>>

    @Query("SELECT * FROM pictures WHERE id = :id")
    suspend fun getPictureById(id: String): PictureEntity?

    @Query("SELECT * FROM pictures WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncPictures(): List<PictureEntity>

    @Query("SELECT * FROM pictures WHERE local_id = :localId")
    suspend fun getPictureByLocalId(localId: String): PictureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: PictureEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictures(pictures: List<PictureEntity>)

    @Update
    suspend fun updatePicture(picture: PictureEntity)

    @Query("UPDATE pictures SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM pictures WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM pictures WHERE foreign_id = :foreignId AND type = :type")
    suspend fun deleteByForeignId(foreignId: String, type: String)
}

