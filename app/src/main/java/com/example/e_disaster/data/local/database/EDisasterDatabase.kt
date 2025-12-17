package com.example.e_disaster.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.e_disaster.data.local.database.converters.EnumConverters
import com.example.e_disaster.data.local.database.dao.DisasterAidDao
import com.example.e_disaster.data.local.database.dao.DisasterDao
import com.example.e_disaster.data.local.database.dao.DisasterReportDao
import com.example.e_disaster.data.local.database.dao.DisasterVictimDao
import com.example.e_disaster.data.local.database.dao.DisasterVolunteerDao
import com.example.e_disaster.data.local.database.dao.NotificationDao
import com.example.e_disaster.data.local.database.dao.PictureDao
import com.example.e_disaster.data.local.database.dao.SyncStatusDao
import com.example.e_disaster.data.local.database.dao.UserDao
import com.example.e_disaster.data.local.database.entities.DisasterAidEntity
import com.example.e_disaster.data.local.database.entities.DisasterEntity
import com.example.e_disaster.data.local.database.entities.DisasterReportEntity
import com.example.e_disaster.data.local.database.entities.DisasterVictimEntity
import com.example.e_disaster.data.local.database.entities.DisasterVolunteerEntity
import com.example.e_disaster.data.local.database.entities.NotificationEntity
import com.example.e_disaster.data.local.database.entities.PictureEntity
import com.example.e_disaster.data.local.database.entities.SyncStatusEntity
import com.example.e_disaster.data.local.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DisasterEntity::class,
        DisasterVolunteerEntity::class,
        DisasterReportEntity::class,
        DisasterVictimEntity::class,
        DisasterAidEntity::class,
        PictureEntity::class,
        NotificationEntity::class,
        SyncStatusEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(EnumConverters::class)
abstract class EDisasterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun disasterDao(): DisasterDao
    abstract fun disasterVolunteerDao(): DisasterVolunteerDao
    abstract fun disasterReportDao(): DisasterReportDao
    abstract fun disasterVictimDao(): DisasterVictimDao
    abstract fun disasterAidDao(): DisasterAidDao
    abstract fun pictureDao(): PictureDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncStatusDao(): SyncStatusDao
}