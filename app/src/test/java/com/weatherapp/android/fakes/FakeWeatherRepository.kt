package com.weatherapp.android.fakes

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.data.repository.WeatherRepository
import com.weatherapp.android.domain.ResolvedLocation
import com.weatherapp.android.domain.WeatherError

class FakeWeatherRepository : WeatherRepository {
    var weatherResult: Result<CurrentWeatherResponse> = Result.failure(WeatherError.Unknown)
    var geocodeResult: Result<ResolvedLocation> = Result.failure(WeatherError.Unknown)

    var lastRequestedCoordinates: Pair<Double, Double>? = null
        private set
    var lastRequestedCity: String? = null
        private set

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        lastRequestedCoordinates = lat to lon
        return weatherResult.getOrThrow()
    }

    override suspend fun geocodeCity(city: String): ResolvedLocation {
        lastRequestedCity = city
        return geocodeResult.getOrThrow()
    }
}
