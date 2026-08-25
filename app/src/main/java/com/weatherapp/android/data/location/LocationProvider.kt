package com.weatherapp.android.data.location

import com.weatherapp.android.domain.Coordinates

interface LocationProvider {
    // safe to call from anywhere, no Activity needed
    fun hasLocationPermission(): Boolean

    // suspends until a location comes back, times out, or throws.
    // caller should confirm hasLocationPermission() first.
    suspend fun getCurrentLocation(): Coordinates
}
