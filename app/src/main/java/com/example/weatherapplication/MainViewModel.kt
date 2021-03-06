package com.example.weatherapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.api.ApiFactory
import com.example.weatherapplication.data.Coordinates
import com.example.weatherapplication.data.WeatherResponse
import com.example.weatherapplication.database.WeatherDatabase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    private val apiFactory = ApiFactory.instance
    private val apiService = apiFactory!!.apiService
    private val weatherDatabase = WeatherDatabase.getDatabase(getApplication())

    private val _weatherResponseData = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> = _weatherResponseData

    private val _currentCoordinates = MutableLiveData<Coordinates>()
    val currentCoordinates: LiveData<Coordinates> = _currentCoordinates

    private val _isRequestFinished = MutableLiveData(true)
    val isRequestFinished: LiveData<Boolean> = _isRequestFinished

    init {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDatabase.databaseDao()?.clearWeatherResponseDatabase()
        }
    }

    fun getWeatherByCity(city: String) {
        _isRequestFinished.postValue(false)

        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse = getWeatherResponseFromDatabase(city)
            if (weatherResponse != null) {
                _weatherResponseData.postValue(weatherResponse)
                _isRequestFinished.postValue(true)
            } else {
                getWeatherResponsFromApi(city)
            }
        }
    }
    private fun getWeatherResponsFromApi(city: String) {
        compositeDisposable.add(
            apiService.getWeatherByCity(city)
                .subscribe(
                    {
                        addWeatherResponseToWeatherDatabase(it)
                        _weatherResponseData.postValue(it)
                        _isRequestFinished.postValue(true)
                    },
                    {
                        _isRequestFinished.postValue(true)
                    })
        )
    }
    private fun getWeatherResponseFromDatabase(name: String): WeatherResponse? {
        return weatherDatabase.databaseDao()?.findWeatherResponseByName(name)
    }
    private fun addWeatherResponseToWeatherDatabase(weatherResponse: WeatherResponse) {
        weatherDatabase.databaseDao()?.insertWeatherResponse(weatherResponse)
    }

    fun getWeatherByCoordinates() {
        _isRequestFinished.postValue(false)

        viewModelScope.launch(Dispatchers.IO) {
            while (currentCoordinates.value == null)
                delay(1000)

            compositeDisposable.add(
                apiService.getWeatherByCoordinates(
                    currentCoordinates.value!!.longitude, currentCoordinates.value!!.lattitude
                )
                    .subscribe(
                        {
                            _weatherResponseData.postValue(it)
                            _isRequestFinished.postValue(true)
                        },
                        {
                            _isRequestFinished.postValue(true)
                        })
            )
        }
    }

    fun setCurrentCoordinates(coordinates: Coordinates) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentCoordinates.postValue(coordinates)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}