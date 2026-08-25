package com.weatherapp.android.ui.weather

/** UI-ready projection of CurrentWeatherResponse — formatted strings only, so the
 *  composables that render it never need to know about the raw API shape. */
data class WeatherUiModel(
    val cityName: String,
    val temperature: String,
    val feelsLike: String,
    val low: String,
    val high: String,
    val humidity: String,
    val windSpeed: String,
    val conditionDescription: String,
    val iconUrl: String?
)
