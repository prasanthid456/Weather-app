package com.weatherapp.android.ui.weather

// formatted strings only - composables shouldn't need to know the raw API shape
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
