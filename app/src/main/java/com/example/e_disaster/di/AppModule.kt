package com.example.e_disaster.di

import android.content.Context
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.remote.service.AuthApiService
import com.example.e_disaster.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // These dependencies will live as long as the app is running
object AppModule {

    // Provides a singleton instance of UserPreferences
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    // Provides a singleton instance of AuthRepository
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: AuthApiService, // Hilt will get this from NetworkModule
        userPreferences: UserPreferences // Hilt will get this from the provider above
    ): AuthRepository {
        return AuthRepository(apiService, userPreferences)
    }
}
