package com.weatherapp.android.domain

/** Result of resolving a typed city name to coordinates via the Geocoding API. */
data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val resolvedCityName: String
)
