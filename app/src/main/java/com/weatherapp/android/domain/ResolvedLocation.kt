package com.weatherapp.android.domain

// result of resolving a typed city name to coordinates via the geocoding API
data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val resolvedCityName: String
)
