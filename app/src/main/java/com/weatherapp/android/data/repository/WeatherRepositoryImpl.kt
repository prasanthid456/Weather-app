package com.weatherapp.android.data.repository

import com.weatherapp.android.data.remote.OpenWeatherApiService
import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.domain.ResolvedLocation
import com.weatherapp.android.domain.WeatherError
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: OpenWeatherApiService,
    @Named("openWeatherApiKey") private val apiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse =
        safeCall { apiService.getCurrentWeather(lat, lon, UNITS_IMPERIAL, apiKey) }

    override suspend fun geocodeCity(city: String): ResolvedLocation {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            throw WeatherError.CityNotFound(null)
        }

        // "City, ST" lets people disambiguate (Springfield, IL vs Springfield, MO).
        // otherwise just treat it as a bare US city name.
        val parts = trimmed.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val cityName = parts.first()
        val stateCode = parts.getOrNull(1)
        val query = if (stateCode != null) "$cityName,$stateCode,US" else "$cityName,US"

        val results = safeCall { apiService.geocodeCity(query, GEOCODE_RESULT_LIMIT, apiKey) }

        // geocoder returns 200 + empty list for no match, not a 404, so
        // this has to be checked manually
        val match = results.firstOrNull() ?: throw WeatherError.CityNotFound(trimmed)
        return ResolvedLocation(match.lat, match.lon, match.name)
    }

    private suspend fun <T> safeCall(block: suspend () -> Response<T>): T {
        val response = try {
            block()
        } catch (e: IOException) {
            // offline / timeout / DNS - probably the most common failure in practice
            throw WeatherError.NoConnectivity
        } catch (e: WeatherError) {
            throw e
        } catch (e: Exception) {
            throw WeatherError.Unknown
        }

        if (response.isSuccessful) {
            return response.body() ?: throw WeatherError.Unknown
        }

        throw when (response.code()) {
            401 -> WeatherError.InvalidApiKey
            404 -> WeatherError.CityNotFound(null)
            else -> WeatherError.Server(response.code())
        }
    }

    private companion object {
        const val UNITS_IMPERIAL = "imperial"
        const val GEOCODE_RESULT_LIMIT = 1
    }
}
