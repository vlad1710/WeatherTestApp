package com.example.weatherapplication.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory private constructor() {
    val apiService: ApiService
        get() = retrofit.create(ApiService::class.java)

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    companion object {
        private var apiFactory: ApiFactory? = null
        private lateinit var retrofit: Retrofit

        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        val instance: ApiFactory?
            get() {
                if (apiFactory == null) {
                    apiFactory = ApiFactory()
                }
                return apiFactory
            }
    }
}