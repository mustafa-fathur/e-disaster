package com.example.e_disaster.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val email: String,
    
    @ColumnInfo(name = "user_type")
    val userType: String, // "admin", "officer", "volunteer"
    
    val status: String, // "registered", "active", "inactive"
    
    val nik: String?,
    val phone: String?,
    val address: String?,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String?,
    
    val gender: String?,
    
    @ColumnInfo(name = "profile_picture")
    val profilePicture: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)

