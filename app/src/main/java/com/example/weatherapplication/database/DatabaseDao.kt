package com.example.weatherapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapplication.data.WeatherResponse

@Dao
interface DatabaseDao {
    @Query("DELETE FROM weather_response_table")
    fun clearWeatherResponseDatabase()

    @Query("SELECT * FROM weather_response_table WHERE name LIKE :name")
    fun findWeatherResponseByName(name: String): WeatherResponse

    @Insert
    fun insertWeatherResponse(weatherResponse: WeatherResponse)
}