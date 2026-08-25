package com.weatherapp.android.ui.weather

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Loaded(val weather: WeatherUiModel) : WeatherUiState
    data class Failed(val message: String) : WeatherUiState
}
