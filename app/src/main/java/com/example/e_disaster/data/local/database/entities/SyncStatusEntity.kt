package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.e_disaster.data.local.database.SyncOperation

@Entity(
    tableName = "sync_status",
    indices = [
        Index(value = ["entity_type", "entity_id"]),
        Index(value = ["operation"])
    ]
)
data class SyncStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entity_type")
    val entityType: String, // "disaster", "report", "victim", "aid", "volunteer", "picture"

    @ColumnInfo(name = "entity_id")
    val entityId: String,

    @ColumnInfo(name = "operation")
    val operation: SyncOperation, // CREATE, UPDATE, DELETE

    @ColumnInfo(name = "local_id")
    val localId: String? = null,

    @ColumnInfo(name = "server_id")
    val serverId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "retry_count")
    val retryCount: Int,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null
)

