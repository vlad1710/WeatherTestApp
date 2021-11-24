package com.example.weatherapplication.api

import com.example.weatherapplication.data.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather?appid=90459dbbab47474b9c961d33dacd55ed&units=metric")
    fun getWeatherByCity(@Query("q") city: String): Single<WeatherResponse>

    @GET("weather?appid=90459dbbab47474b9c961d33dacd55ed&units=metric")
    fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<WeatherResponse>
}