package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "disaster_reports",
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
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DisasterReportEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "disaster_id")
    val disasterId: String?,

    @ColumnInfo(name = "reported_by")
    val reportedBy: String?,

    val title: String?,
    val description: String?,
    val lat: Double?,
    @ColumnInfo(name = "long")
    val longitude: Double?,
    
    @ColumnInfo(name = "is_final_stage")
    val isFinalStage: Boolean,

    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.SYNCED,

    @ColumnInfo(name = "local_id")
    val localId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)

