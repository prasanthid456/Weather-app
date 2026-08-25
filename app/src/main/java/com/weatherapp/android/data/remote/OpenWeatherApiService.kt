package com.weatherapp.android.data.remote

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.data.remote.model.GeocodingResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service definition for the two OpenWeatherMap endpoints this app
 * uses. City-name search always goes through [geocodeCity] first —
 * OpenWeatherMap's docs say direct city-name lookups on /data/2.5/weather
 * are deprecated and no longer maintained.
 *
 * Returns [Response] (rather than the bare body) so [WeatherRepositoryImpl]
 * can inspect the HTTP status code and map it to a specific [WeatherError][
 * com.weatherapp.android.domain.WeatherError] instead of just a generic
 * failure.
 */
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
