package com.example.e_disaster.di

import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.remote.UnauthorizedHandler
import com.example.e_disaster.data.remote.service.AuthApiService
import com.example.e_disaster.data.remote.service.DisasterAidApiService
import com.example.e_disaster.data.remote.service.DisasterApiService
import com.example.e_disaster.data.remote.service.DisasterVictimApiService
import com.example.e_disaster.data.remote.service.PictureApiService
import com.example.e_disaster.utils.Constants.API_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        userPreferences: UserPreferences,
        unauthorizedHandler: UnauthorizedHandler
    ): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking { userPreferences.authToken.first() }

            android.util.Log.d("AuthInterceptor", "Token: Bearer $token")

            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader("Accept", "application/json")

            token?.let {
                if (it.isNotBlank()) {
                    android.util.Log.d("AuthInterceptor", "Attaching Token: Bearer $it")
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }

            try {
                chain.proceed(requestBuilder.build())
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    unauthorizedHandler.trigger()
                }
                throw e
            }
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
            .baseUrl(API_BASE_URL)
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

    @Provides
    @Singleton
    fun providePictureApiService(retrofit: Retrofit): PictureApiService {
        return retrofit.create(PictureApiService::class.java)
    }
}
