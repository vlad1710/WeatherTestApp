package com.example.weatherapplication

import android.app.Application
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.api.ApiFactory
import com.example.weatherapplication.data.Coordinates
import com.example.weatherapplication.data.WeatherResponse
import com.example.weatherapplication.database.WeatherDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    val apiFactory = ApiFactory.instance
    val apiService = apiFactory!!.apiService
    val database = WeatherDatabase.getDatabase(getApplication())

    private val _data = MutableLiveData<WeatherResponse>()
    val data: LiveData<WeatherResponse> = _data


    fun getWeatherByCity(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse = getWeatherResponseFromDatabase(city)

            if (weatherResponse != null) {
                Log.i("kkk", "from DB " + weatherResponse.toString()) // todo log
                _data.postValue(weatherResponse)
            } else {
                getWeatherResponsFromApi(city)
            }
        }
    }
    private fun getWeatherResponsFromApi (city: String) {
        compositeDisposable.add(
            apiService.getWeatherByCity(city)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.i("kkk", "from INTERNET " + it.toString()) // todo log
                        addWeatherResponse(it)
                        _data.postValue(it)
                    },
                    {
                    })
        )
    }
    private fun getWeatherResponseFromDatabase(name: String) :WeatherResponse? {
        return database.databaseDao()?.findWeatherResponseByName(name)
    }
    private fun addWeatherResponse(weatherResponse: WeatherResponse) {
        database.databaseDao()?.insertWeatherResponse(weatherResponse)
    }

    fun getWeatherByCoordinates(coordinates: Coordinates) {
        compositeDisposable.add(
            apiService.getWeatherByCoordinates(coordinates.lattitude, coordinates.longitude)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        _data.postValue(it)
                    },
                    {
                    })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        CoroutineScope(Dispatchers.IO).launch {
            database.databaseDao()?.clearWeatherResponseDatabase()
        }
    }
}