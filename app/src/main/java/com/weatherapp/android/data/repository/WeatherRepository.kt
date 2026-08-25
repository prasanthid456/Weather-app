package com.weatherapp.android.data.repository

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.domain.ResolvedLocation

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse
    suspend fun geocodeCity(city: String): ResolvedLocation
}
