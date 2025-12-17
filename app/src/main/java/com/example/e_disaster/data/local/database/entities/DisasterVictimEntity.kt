package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "disaster_victims",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["disaster_id"]),
        Index(value = ["sync_status"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = DisasterEntity::class,
            parentColumns = ["id"],
            childColumns = ["disaster_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DisasterVictimEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "disaster_id")
    val disasterId: String?,

    @ColumnInfo(name = "reported_by")
    val reportedBy: String?,

    val nik: String?,
    val name: String?,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    
    val gender: String?, // "Laki-laki" or "Perempuan" or boolean as string
    
    @ColumnInfo(name = "contact_info")
    val contactInfo: String?,
    
    val description: String?,
    
    @ColumnInfo(name = "is_evacuated")
    val isEvacuated: Boolean,
    
    val status: String?, // "luka ringan", "luka berat", "meninggal", "hilang"
    
    @ColumnInfo(name = "reporter_name")
    val reporterName: String?,

    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus,

    @ColumnInfo(name = "local_id")
    val localId: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long?
)

