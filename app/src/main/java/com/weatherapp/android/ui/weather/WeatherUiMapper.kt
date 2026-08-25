package com.weatherapp.android.ui.weather

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.util.WeatherFormatUtils

/**
 * Turns the raw (Java) API model into UI-ready strings via the Java
 * [WeatherFormatUtils] helpers. Keeping this here, rather than formatting
 * inline in a composable, is what lets [WeatherUiMapperTest] verify
 * formatting without touching Compose at all.
 */
object WeatherUiMapper {
    fun map(response: CurrentWeatherResponse): WeatherUiModel {
        val condition = response.weather?.firstOrNull()
        return WeatherUiModel(
            cityName = response.name,
            temperature = WeatherFormatUtils.formatTemperature(response.main.temp),
            feelsLike = "Feels like " + WeatherFormatUtils.formatTemperature(response.main.feelsLike),
            low = WeatherFormatUtils.formatTemperature(response.main.tempMin),
            high = WeatherFormatUtils.formatTemperature(response.main.tempMax),
            humidity = "${response.main.humidity}%",
            windSpeed = WeatherFormatUtils.formatWindSpeed(response.wind.speed),
            conditionDescription = condition?.description?.let { WeatherFormatUtils.capitalize(it) } ?: "—",
            iconUrl = condition?.icon?.let { WeatherFormatUtils.buildIconUrl(it) }
        )
    }
}
