package com.example.weatherapplication

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.data.Coordinates
import com.example.weatherapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var weatherViewModel: WeatherViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var locationManager: LocationManager
    var coordinates = Coordinates(0.0, 0.0)
    var cities = arrayListOf("London", "Paris", "Istanbul", "Tokyo", "Kyiv")
    var currentCity = "London"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spinnerSettings()
        requestPermissions()

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        binding.button.setOnClickListener {
            if (!currentCity.equals("My Location")){
                weatherViewModel.getWeatherByCity(currentCity)
            } else {
                getMyLocation()
            }
        }

        weatherViewModel.data.observe(this, {
            binding.textViewWeatherResponse.text = it.toString()
        })
    }

    private fun spinnerSettings() {
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            cities
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        binding.spinnerCities.adapter = adapter
        binding.spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentCity = cities.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun getMyLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            100,
            10f,
            {
                coordinates.lattitude = it.latitude
                coordinates.longitude = it.longitude
                weatherViewModel.getWeatherByCoordinates(coordinates)
                Log.i("kkk", it.latitude.toString()) // todo log
                Log.i("kkk", it.longitude.toString())
            })
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 789
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 789) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cities.add("My Location")
                getMyLocation()
            } else {
                Toast.makeText(this, "Enable location permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }
}