package com.example.e_disaster.di

import android.content.Context
import androidx.room.Room
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.local.database.EDisasterDatabase
import com.example.e_disaster.data.local.database.dao.DisasterAidDao
import com.example.e_disaster.data.local.database.dao.DisasterDao
import com.example.e_disaster.data.local.database.dao.DisasterReportDao
import com.example.e_disaster.data.local.database.dao.DisasterVictimDao
import com.example.e_disaster.data.local.database.dao.DisasterVolunteerDao
import com.example.e_disaster.data.local.database.dao.NotificationDao
import com.example.e_disaster.data.local.database.dao.PictureDao
import com.example.e_disaster.data.local.database.dao.SyncStatusDao
import com.example.e_disaster.data.local.database.dao.UserDao
import com.example.e_disaster.data.local.storage.ImageManager
import com.example.e_disaster.data.local.sync.NetworkMonitor
import com.example.e_disaster.data.local.sync.SyncManager
import com.example.e_disaster.data.remote.service.AuthApiService
import com.example.e_disaster.data.remote.service.DisasterApiService
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import com.example.e_disaster.data.repository.AuthRepository
import com.example.e_disaster.data.repository.DisasterRepository
import com.example.e_disaster.data.repository.DisasterVictimRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideEDisasterDatabase(@ApplicationContext context: Context): EDisasterDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            EDisasterDatabase::class.java,
            "e_disaster.db"
        )
        .fallbackToDestructiveMigration() // Wipes database on schema change (for development only)
        .build()
    }

    // DAO Providers
    @Provides
    fun provideUserDao(database: EDisasterDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideDisasterDao(database: EDisasterDatabase): DisasterDao {
        return database.disasterDao()
    }

    @Provides
    fun provideDisasterVolunteerDao(database: EDisasterDatabase): DisasterVolunteerDao {
        return database.disasterVolunteerDao()
    }

    @Provides
    fun provideDisasterReportDao(database: EDisasterDatabase): DisasterReportDao {
        return database.disasterReportDao()
    }

    @Provides
    fun provideDisasterVictimDao(database: EDisasterDatabase): DisasterVictimDao {
        return database.disasterVictimDao()
    }

    @Provides
    fun provideDisasterAidDao(database: EDisasterDatabase): DisasterAidDao {
        return database.disasterAidDao()
    }

    @Provides
    fun providePictureDao(database: EDisasterDatabase): PictureDao {
        return database.pictureDao()
    }

    @Provides
    fun provideNotificationDao(database: EDisasterDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun provideSyncStatusDao(database: EDisasterDatabase): SyncStatusDao {
        return database.syncStatusDao()
    }

    // Repository Providers (will be updated in Phase 2 to use DAOs)
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: AuthApiService,
        userPreferences: UserPreferences,
        userDao: UserDao,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepository(apiService, userPreferences, userDao, context)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideImageManager(@ApplicationContext context: Context): ImageManager {
        return ImageManager(context)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        networkMonitor: NetworkMonitor,
        disasterDao: DisasterDao,
        disasterVictimDao: DisasterVictimDao,
        pictureDao: PictureDao,
        disasterApiService: DisasterApiService,
        disasterVictimApiService: DisasterVictimApiService,
        pictureApiService: PictureApiService,
        imageManager: ImageManager
    ): SyncManager {
        return SyncManager(
            networkMonitor,
            disasterDao,
            disasterVictimDao,
            pictureDao,
            disasterApiService,
            disasterVictimApiService,
            pictureApiService,
            imageManager
        )
    }

    @Provides
    @Singleton
    fun provideDisasterRepository(
        apiService: DisasterApiService,
        disasterDao: DisasterDao,
        volunteerDao: DisasterVolunteerDao,
        userDao: UserDao,
        networkMonitor: NetworkMonitor,
        syncManager: SyncManager
    ): DisasterRepository {
        return DisasterRepository(apiService, disasterDao, volunteerDao, userDao, networkMonitor, syncManager)
    }

    @Provides
    @Singleton
    fun provideVictimRepository(
        apiService: DisasterVictimApiService,
        pictureApiService: PictureApiService,
        victimDao: DisasterVictimDao,
        networkMonitor: NetworkMonitor,
        imageManager: ImageManager,
        pictureDao: PictureDao
    ): DisasterVictimRepository {
        return DisasterVictimRepository(apiService, pictureApiService, victimDao, networkMonitor, imageManager, pictureDao)
    }
}
