package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "disaster_aids",
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
data class DisasterAidEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "disaster_id")
    val disasterId: String?,

    @ColumnInfo(name = "reported_by")
    val reportedBy: String?,

    val name: String?,
    val category: String?, // "food", "clothing", "housing"
    val quantity: Int?,
    val description: String?,
    val location: String?,
    val lat: Double?,
    @ColumnInfo(name = "long")
    val longitude: Double?,
    
    @ColumnInfo(name = "donator_name")
    val donatorName: String?,
    
    @ColumnInfo(name = "donator_location")
    val donatorLocation: String?,
    
    @ColumnInfo(name = "donator_lat")
    val donatorLat: Double?,
    
    @ColumnInfo(name = "donator_long")
    val donatorLong: Double?,

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

