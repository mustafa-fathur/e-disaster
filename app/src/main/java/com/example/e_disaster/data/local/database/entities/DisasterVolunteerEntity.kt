package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "disaster_volunteers",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["disaster_id"]),
        Index(value = ["user_id"]),
        Index(value = ["sync_status"]),
        Index(value = ["disaster_id", "user_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = DisasterEntity::class,
            parentColumns = ["id"],
            childColumns = ["disaster_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DisasterVolunteerEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "disaster_id")
    val disasterId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

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

