package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "disasters",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["status"]),
        Index(value = ["sync_status"])
    ]
)
data class DisasterEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "reported_by")
    val reportedBy: String?,
    val source: String,
    val types: String,
    val status: String,
    val title: String,
    val description: String?,
    val date: String?,
    val time: String?,
    val location: String?,
    val coordinate: String?,
    val lat: Double?,
    @ColumnInfo(name = "long")
    val longitude: Double?,
    val magnitude: Double?,
    val depth: Double?,

    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.SYNCED,

    @ColumnInfo(name = "local_id")
    val localId: String? = null, // For offline-created records

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)