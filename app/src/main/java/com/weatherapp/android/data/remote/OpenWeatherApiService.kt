package com.weatherapp.android.data.remote

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.data.remote.model.GeocodingResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// City search always goes through geocodeCity first since OpenWeatherMap
// deprecated direct city-name lookups on /data/2.5/weather.
// Returns Response<T> instead of the bare body so WeatherRepositoryImpl can
// look at the actual HTTP status and map it to a specific WeatherError.
interface OpenWeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>

    @GET("geo/1.0/direct")
    suspend fun geocodeCity(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<GeocodingResult>>
}
