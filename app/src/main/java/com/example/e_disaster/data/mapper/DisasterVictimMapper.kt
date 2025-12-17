package com.example.e_disaster.data.mapper

import com.example.e_disaster.data.local.database.SyncStatus
import com.example.e_disaster.data.local.database.entities.DisasterVictimEntity
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDetailDto
import com.example.e_disaster.data.remote.dto.disaster_victim.DisasterVictimDto
import com.example.e_disaster.data.remote.dto.disaster_victim.VictimPictureDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DisasterVictimMapper {

    // Entity → Model
    fun DisasterVictimEntity.toModel(pictures: List<VictimPicture>? = null): DisasterVictim {
        return DisasterVictim(
            id = this.id,
            disasterId = this.disasterId,
            reportedBy = this.reportedBy,
            nik = this.nik ?: "",
            name = this.name ?: "",
            dateOfBirth = this.dateOfBirth ?: "",
            gender = this.gender ?: "",
            contactInfo = this.contactInfo ?: "",
            description = this.description ?: "",
            isEvacuated = this.isEvacuated,
            status = this.status ?: "",
            createdAt = formatTimestamp(this.createdAt),
            reporterName = this.reporterName ?: "",
            pictures = pictures,
            updatedAt = formatTimestamp(this.updatedAt)
        )
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Model → Entity
    fun DisasterVictim.toEntity(
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        localId: String? = null,
        createdAt: Long? = null,
        updatedAt: Long? = null,
        lastSyncedAt: Long? = null
    ): DisasterVictimEntity {
        val now = System.currentTimeMillis()
        return DisasterVictimEntity(
            id = this.id,
            disasterId = this.disasterId,
            reportedBy = this.reportedBy,
            nik = this.nik.takeIf { it.isNotEmpty() },
            name = this.name.takeIf { it.isNotEmpty() },
            dateOfBirth = this.dateOfBirth.takeIf { it.isNotEmpty() },
            gender = this.gender.takeIf { it.isNotEmpty() },
            contactInfo = this.contactInfo.takeIf { it.isNotEmpty() },
            description = this.description.takeIf { it.isNotEmpty() },
            isEvacuated = this.isEvacuated,
            status = this.status.takeIf { it.isNotEmpty() },
            reporterName = this.reporterName.takeIf { it.isNotEmpty() },
            syncStatus = syncStatus,
            localId = localId,
            createdAt = createdAt ?: now,
            updatedAt = updatedAt ?: now,
            lastSyncedAt = lastSyncedAt
        )
    }

    // DTO → Entity
    fun DisasterVictimDto.toEntity(
        disasterId: String? = null,
        reportedBy: String? = null,
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        lastSyncedAt: Long? = null
    ): DisasterVictimEntity {
        val now = System.currentTimeMillis()
        val createdAt = this.createdAt?.let { parseDateString(it) } ?: now

        return DisasterVictimEntity(
            id = this.id ?: "",
            disasterId = disasterId,
            reportedBy = reportedBy,
            nik = this.nik,
            name = this.name,
            dateOfBirth = this.dateOfBirth,
            gender = this.gender?.let { if (it) "Perempuan" else "Laki-laki" },
            contactInfo = this.contactInfo,
            description = this.description,
            isEvacuated = this.isEvacuated ?: false,
            status = this.status,
            reporterName = this.reporter?.user?.name,
            syncStatus = syncStatus,
            localId = null,
            createdAt = createdAt,
            updatedAt = createdAt,
            lastSyncedAt = lastSyncedAt ?: now
        )
    }

    // Detail DTO → Entity
    fun DisasterVictimDetailDto.toEntity(
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        lastSyncedAt: Long? = null
    ): DisasterVictimEntity {
        val now = System.currentTimeMillis()
        val createdAt = this.createdAt?.let { parseDateString(it) } ?: now
        val updatedAt = this.updatedAt?.let { parseDateString(it) } ?: createdAt

        return DisasterVictimEntity(
            id = this.id ?: "",
            disasterId = this.disasterId,
            reportedBy = this.reportedBy,
            nik = this.nik,
            name = this.name,
            dateOfBirth = this.dateOfBirth,
            gender = this.gender?.let { if (it) "Perempuan" else "Laki-laki" },
            contactInfo = this.contactInfo,
            description = this.description,
            isEvacuated = this.isEvacuated ?: false,
            status = this.status,
            reporterName = this.reporterName,
            syncStatus = syncStatus,
            localId = null,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastSyncedAt = lastSyncedAt ?: now
        )
    }

    // DTO → Model
    fun DisasterVictimDetailDto.toModel(): DisasterVictim {
        return DisasterVictim(
            id = this.id ?: "",
            disasterId = this.disasterId,
            reportedBy = this.reportedBy,
            nik = this.nik ?: "",
            name = this.name ?: "",
            dateOfBirth = this.dateOfBirth ?: "",
            gender = if (this.gender == true) "Perempuan" else "Laki-laki",
            contactInfo = this.contactInfo ?: "",
            description = this.description ?: "",
            isEvacuated = this.isEvacuated ?: false,
            status = this.status ?: "",
            createdAt = this.createdAt ?: "",
            reporterName = this.reporterName ?: "",
            pictures = this.pictures?.map { it.toModel() },
            updatedAt = this.updatedAt
        )
    }

    // Picture DTO → Model
    fun VictimPictureDto.toModel(): VictimPicture {
        return VictimPicture(
            id = this.id ?: "",
            url = this.url ?: this.filePath ?: "",
            localPath = null, // Remote pictures don't have local path
            caption = this.caption,
            mimeType = this.mimeType ?: "image/jpeg"
        )
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
                    java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
                        .parse(dateString)?.time ?: continue
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

