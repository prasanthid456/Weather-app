package com.weatherapp.android.data.location

import com.weatherapp.android.domain.Coordinates

interface LocationProvider {
    /** Pure permission check — safe to call from anywhere, no Activity needed. */
    fun hasLocationPermission(): Boolean

    /** Suspends until a location arrives, times out, or fails. Caller must
     *  have already confirmed [hasLocationPermission] is true. */
    suspend fun getCurrentLocation(): Coordinates
}
