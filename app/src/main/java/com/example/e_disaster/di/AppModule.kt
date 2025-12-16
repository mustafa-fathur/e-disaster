package com.example.e_disaster.di

import android.content.Context
import com.example.e_disaster.data.local.UserPreferences
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
    fun provideAuthRepository(
        apiService: AuthApiService,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideDisasterRepository(
        apiService: DisasterApiService
    ): DisasterRepository {
        return DisasterRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideVictimRepository(apiService: DisasterVictimApiService, pictureApiService: PictureApiService): DisasterVictimRepository {
        return DisasterVictimRepository(apiService, pictureApiService)
    }
}
