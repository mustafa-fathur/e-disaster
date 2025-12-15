package com.example.e_disaster.di

import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.remote.service.AuthApiService
import com.example.e_disaster.data.remote.service.DisasterAidApiService
import com.example.e_disaster.data.remote.service.DisasterApiService
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://e-disaster.fathur.tech/api/v1/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking { userPreferences.authToken.first() }

            android.util.Log.d("AuthInterceptor", "Token: Bearer $token")

            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDisasterApiService(retrofit: Retrofit): DisasterApiService {
        return retrofit.create(DisasterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDisasterAidApiService(retrofit: Retrofit): DisasterAidApiService {
        return retrofit.create(DisasterAidApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDisasterVictimApiService(retrofit: Retrofit): DisasterVictimApiService {
        return retrofit.create(DisasterVictimApiService::class.java)
    }
}
