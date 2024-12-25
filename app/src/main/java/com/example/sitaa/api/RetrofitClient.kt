package com.example.sitaa.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api/"
    const val FILE_BASE_URL = "http://10.0.2.2:3000/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            // Handle file URLs
            val newUrl = if (original.url.toString().contains("uploads/")) {
                original.url.newBuilder()
                    .scheme("http")
                    .host("10.0.2.2")
                    .port(8000)
                    .build()
            } else {
                original.url
            }

            val request = original.newBuilder()
                .url(newUrl)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun createService(): ApiService = retrofit.create(ApiService::class.java)

    fun getFullFileUrl(path: String?): String {
        return if (!path.isNullOrBlank() && path.startsWith("uploads/")) {
            FILE_BASE_URL + path
        } else {
            path ?: ""
        }
    }
}