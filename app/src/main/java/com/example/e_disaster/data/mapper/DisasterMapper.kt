package com.example.e_disaster.data.mapper

import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterEntity
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DisasterMapper {

    // Entity → Model
    fun DisasterEntity.toModel(): Disaster {
        return Disaster(
            id = this.id,
            reportedBy = this.reportedBy,
            source = this.source,
            types = this.types,
            status = this.status,
            title = this.title,
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            lat = this.lat,
            long = this.longitude,
            magnitude = this.magnitude,
            depth = this.depth,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }

    // Model → Entity
    fun Disaster.toEntity(
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        localId: String? = null,
        createdAt: Long? = null,
        updatedAt: Long? = null,
        lastSyncedAt: Long? = null
    ): DisasterEntity {
        val now = System.currentTimeMillis()
        return DisasterEntity(
            id = this.id ?: localId ?: "",
            reportedBy = this.reportedBy,
            source = this.source ?: "manual",
            types = this.types ?: "",
            status = this.status ?: "ongoing",
            title = this.title ?: "Tanpa Judul",
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            coordinate = if (this.lat != null && this.long != null) {
                "${this.lat}, ${this.long}"
            } else null,
            lat = this.lat,
            longitude = this.long,
            magnitude = this.magnitude,
            depth = this.depth,
            syncStatus = syncStatus,
            localId = localId,
            createdAt = createdAt ?: now,
            updatedAt = updatedAt ?: now,
            lastSyncedAt = lastSyncedAt
        )
    }

    // DTO → Entity
    fun DisasterDto.toEntity(
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        lastSyncedAt: Long? = null
    ): DisasterEntity {
        val now = System.currentTimeMillis()
        val createdAt = this.createdAt?.let { parseDateString(it) } ?: now
        val updatedAt = this.updatedAt?.let { parseDateString(it) } ?: now
        val syncedAt = lastSyncedAt ?: now

        return DisasterEntity(
            id = this.id ?: "",
            reportedBy = this.reportedBy,
            source = this.source ?: "manual",
            types = this.types ?: "",
            status = this.status ?: "ongoing",
            title = this.title ?: "Tanpa Judul",
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            coordinate = if (this.lat != null && this.long != null) {
                "${this.lat}, ${this.long}"
            } else null,
            lat = this.lat,
            longitude = this.long,
            magnitude = this.magnitude,
            depth = this.depth,
            syncStatus = syncStatus,
            localId = null,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastSyncedAt = syncedAt
        )
    }

    // DTO → Model
    fun DisasterDto.toModel(): Disaster {
        return Disaster(
            id = this.id,
            reportedBy = this.reportedBy,
            source = this.source,
            types = this.types,
            status = this.status,
            title = this.title,
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            lat = this.lat,
            long = this.long,
            magnitude = this.magnitude,
            depth = this.depth,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    // Entity → DTO (for syncing)
    fun DisasterEntity.toDto(): DisasterDto {
        return DisasterDto(
            id = this.id,
            reportedBy = this.reportedBy,
            source = this.source,
            types = this.types,
            status = this.status,
            title = this.title,
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            lat = this.lat,
            long = this.longitude,
            magnitude = this.magnitude,
            depth = this.depth,
            createdAt = formatTimestamp(this.createdAt),
            updatedAt = formatTimestamp(this.updatedAt)
        )
    }

    // Model → DTO (for creating/updating)
    fun Disaster.toDto(): DisasterDto {
        return DisasterDto(
            id = this.id,
            reportedBy = this.reportedBy,
            source = this.source,
            types = this.types,
            status = this.status,
            title = this.title,
            description = this.description,
            date = this.date,
            time = this.time,
            location = this.location,
            lat = this.lat,
            long = this.long,
            magnitude = this.magnitude,
            depth = this.depth,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun parseDateString(dateString: String): Long {
        return try {
            val formats = listOf(
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
            )
            for (format in formats) {
                try {
                    SimpleDateFormat(format, Locale.getDefault()).parse(dateString)?.time
                        ?: continue
                } catch (e: Exception) {
                    continue
                }
            }
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

