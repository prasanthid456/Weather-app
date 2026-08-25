package com.weatherapp.android.ui.weather

import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.util.WeatherFormatUtils

// Turns the raw API model into display-ready strings using WeatherFormatUtils.
// Kept out of the composables so formatting can be tested without Compose.
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
