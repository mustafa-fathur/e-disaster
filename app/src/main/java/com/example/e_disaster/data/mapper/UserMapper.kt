package com.example.e_disaster.data.mapper

import com.example.e_disaster.data.local.database.entities.UserEntity
import com.example.e_disaster.data.model.User
import com.example.e_disaster.data.remote.dto.auth.UserDto
import java.text.SimpleDateFormat
import java.util.Locale

object UserMapper {

    // Entity → Model
    fun UserEntity.toModel(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            userType = this.userType,
            status = this.status,
            nik = this.nik ?: "",
            phone = this.phone ?: "",
            address = this.address ?: "",
            dateOfBirth = this.dateOfBirth ?: "",
            gender = this.gender ?: "",
            profilePicture = this.profilePicture ?: ""
        )
    }

    // Model → Entity
    fun User.toEntity(
        createdAt: Long? = null,
        updatedAt: Long? = null,
        lastSyncedAt: Long? = null
    ): UserEntity {
        val now = System.currentTimeMillis()
        return UserEntity(
            id = this.id,
            name = this.name,
            email = this.email,
            userType = this.userType,
            status = this.status,
            nik = this.nik.takeIf { it.isNotEmpty() },
            phone = this.phone.takeIf { it.isNotEmpty() },
            address = this.address.takeIf { it.isNotEmpty() },
            dateOfBirth = this.dateOfBirth.takeIf { it.isNotEmpty() },
            gender = this.gender.takeIf { it.isNotEmpty() },
            profilePicture = this.profilePicture.takeIf { it.isNotEmpty() },
            createdAt = createdAt ?: now,
            updatedAt = updatedAt ?: now,
            lastSyncedAt = lastSyncedAt ?: now
        )
    }

    // DTO → Entity
    fun UserDto.toEntity(
        lastSyncedAt: Long? = null
    ): UserEntity {
        val now = System.currentTimeMillis()
        return UserEntity(
            id = this.id,
            name = this.name,
            email = this.email,
            userType = this.type,
            status = this.status,
            nik = this.nik,
            phone = this.phone,
            address = this.address,
            dateOfBirth = this.dateOfBirth,
            gender = if (this.gender == false) "Laki-laki" else "Perempuan",
            profilePicture = this.profilePicture?.url?.let { "https://e-disaster.fathur.tech$it" },
            createdAt = now,
            updatedAt = now,
            lastSyncedAt = lastSyncedAt ?: now
        )
    }

    // DTO → Model
    fun UserDto.toModel(): User {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))

        val formattedDate = try {
            val date = inputFormatter.parse(this.dateOfBirth ?: "")
            date?.let { outputFormatter.format(it) } ?: "Tidak ada data"
        } catch (e: Exception) {
            "Tidak ada data"
        }

        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            userType = this.type,
            status = this.status,
            nik = this.nik ?: "Tidak ada data",
            phone = this.phone ?: "Tidak ada data",
            address = this.address ?: "Tidak ada data",
            dateOfBirth = formattedDate,
            gender = if (this.gender == false) "Laki-laki" else "Perempuan",
            profilePicture = this.profilePicture?.url?.let { "https://e-disaster.fathur.tech$it" } ?: "Tidak ada data"
        )
    }
}

