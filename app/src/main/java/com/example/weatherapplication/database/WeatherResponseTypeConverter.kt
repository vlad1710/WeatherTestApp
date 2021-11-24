package com.example.weatherapplication.database

import androidx.room.TypeConverter
import com.example.weatherapplication.data.WeatherParam
import com.google.gson.Gson

class WeatherResponseTypeConverter {

    @TypeConverter
    fun weatherParamConverterToString(weatherParam: WeatherParam): String{
        return Gson().toJson(weatherParam)
    }

    @TypeConverter
    fun jsonToParamConverter(jsonString: String): WeatherParam {
        return Gson().fromJson(jsonString, WeatherParam::class.java)
    }
}