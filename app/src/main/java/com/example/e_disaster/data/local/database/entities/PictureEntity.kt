package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncStatus

@Entity(
    tableName = "pictures",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["foreign_id"]),
        Index(value = ["sync_status"])
    ]
)
data class PictureEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "foreign_id")
    val foreignId: String, // disaster_id, report_id, victim_id, aid_id, user_id

    val type: String, // "profile", "disaster", "report", "victim", "aid"
    
    val caption: String?,
    
    @ColumnInfo(name = "local_path")
    val localPath: String?, // Local file path
    
    @ColumnInfo(name = "server_url")
    val serverUrl: String?, // Server URL after upload
    
    @ColumnInfo(name = "file_path")
    val filePath: String?, // Server file path (from API)
    
    @ColumnInfo(name = "mime_type")
    val mimeType: String?,
    
    @ColumnInfo(name = "alt_text")
    val altText: String?,

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

