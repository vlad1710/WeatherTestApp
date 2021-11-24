package com.example.weatherapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapplication.database.WeatherResponseTypeConverter

@Entity(tableName = "weather_response_table")
@TypeConverters(value = [WeatherResponseTypeConverter::class])
data class WeatherResponse(
    @PrimaryKey
    val main: WeatherParam,
    val name: String
)
