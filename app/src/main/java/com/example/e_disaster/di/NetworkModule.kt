package com.example.e_disaster.di

import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.repository.AuthRepository
import com.example.e_disaster.data.remote.service.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Using the BASE_URL from your AGENT.md
    private const val BASE_URL = "http://10.0.2.2:8000/api/v1/"

    // Provides the logging interceptor for debugging network requests
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    // Provides the authentication interceptor. This is the new, Hilt-powered way.
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): Interceptor { // <-- DEPENDS ON UserPreferences
        return Interceptor { chain ->
            // Use runBlocking to synchronously get the token directly from DataStore.
            val token = runBlocking { userPreferences.authToken.first() } // <-- USES UserPreferences

            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }
    }


    // Provides the OkHttpClient, including both interceptors
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    // Provides the Retrofit instance
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides the AuthApiService implementation
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // You can add providers for other API services here in the future
    // e.g., fun provideDisasterApiService(...)
}
