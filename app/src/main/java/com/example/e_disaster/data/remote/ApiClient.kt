package com.example.e_disaster.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://bekendisaster.siunand.my.id/api/v1/" // Changed to local development API

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = getAuthToken() // Implementasi untuk mendapatkan token dari SharedPreferences atau secure storage

        val requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")

        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val disasterAidApi: DisasterAidApi = retrofit.create(DisasterAidApi::class.java)

    private fun getAuthToken(): String? {
        // TODO: Implementasi untuk mendapatkan token dari SharedPreferences atau secure storage
        // Contoh:
        // return SharedPreferencesManager.getAuthToken()
        return null // Placeholder
    }
}
