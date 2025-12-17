package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterReportDao {
    @Query("SELECT * FROM disaster_reports WHERE disaster_id = :disasterId ORDER BY created_at DESC")
    fun getReportsByDisasterId(disasterId: String): Flow<List<DisasterReportEntity>>

    @Query("SELECT * FROM disaster_reports WHERE id = :id")
    suspend fun getReportById(id: String): DisasterReportEntity?

    @Query("SELECT * FROM disaster_reports WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncReports(): List<DisasterReportEntity>

    @Query("SELECT * FROM disaster_reports WHERE local_id = :localId")
    suspend fun getReportByLocalId(localId: String): DisasterReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DisasterReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<DisasterReportEntity>)

    @Update
    suspend fun updateReport(report: DisasterReportEntity)

    @Query("UPDATE disaster_reports SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM disaster_reports WHERE id = :id")
    suspend fun deleteById(id: String)
}

