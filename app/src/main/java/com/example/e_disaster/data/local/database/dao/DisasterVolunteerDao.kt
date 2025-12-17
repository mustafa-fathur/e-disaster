package com.example.e_disaster.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterVolunteerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterVolunteerDao {
    @Query("SELECT * FROM disaster_volunteers WHERE disaster_id = :disasterId")
    fun getVolunteersByDisasterId(disasterId: String): Flow<List<DisasterVolunteerEntity>>

    @Query("SELECT * FROM disaster_volunteers WHERE user_id = :userId")
    fun getDisastersByUserId(userId: String): Flow<List<DisasterVolunteerEntity>>

    @Query("SELECT * FROM disaster_volunteers WHERE disaster_id = :disasterId AND user_id = :userId")
    suspend fun isUserAssigned(disasterId: String, userId: String): DisasterVolunteerEntity?

    @Query("SELECT * FROM disaster_volunteers WHERE id = :id")
    suspend fun getVolunteerById(id: String): DisasterVolunteerEntity?

    @Query("SELECT * FROM disaster_volunteers WHERE sync_status != 'SYNCED'")
    suspend fun getPendingSyncVolunteers(): List<DisasterVolunteerEntity>

    @Query("SELECT * FROM disaster_volunteers WHERE local_id = :localId")
    suspend fun getVolunteerByLocalId(localId: String): DisasterVolunteerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: DisasterVolunteerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteers(volunteers: List<DisasterVolunteerEntity>)

    @Update
    suspend fun updateVolunteer(volunteer: DisasterVolunteerEntity)

    @Query("UPDATE disaster_volunteers SET sync_status = :status, last_synced_at = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, timestamp: Long)

    @Query("DELETE FROM disaster_volunteers WHERE disaster_id = :disasterId AND user_id = :userId")
    suspend fun deleteByDisasterAndUser(disasterId: String, userId: String)

    @Query("DELETE FROM disaster_volunteers WHERE id = :id")
    suspend fun deleteById(id: String)
}

