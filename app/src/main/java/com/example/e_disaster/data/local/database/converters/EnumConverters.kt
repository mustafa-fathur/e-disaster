package com.example.e_disaster.data.local.database.converters

import androidx.room.TypeConverter
import com.example.e_disaster.data.local.database.SyncOperation
import com.example.e_disaster.data.local.database.SyncStatus

class EnumConverters {
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun fromSyncOperation(value: SyncOperation): String = value.name

    @TypeConverter
    fun toSyncOperation(value: String): SyncOperation = SyncOperation.valueOf(value)
}