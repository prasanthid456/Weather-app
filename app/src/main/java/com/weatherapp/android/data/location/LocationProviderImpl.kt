package com.weatherapp.android.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.weatherapp.android.domain.Coordinates
import com.weatherapp.android.domain.WeatherError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Wraps the plain `android.location` APIs (rather than Play Services'
 * FusedLocationProviderClient) so the app has no Google Play Services
 * dependency for a single "get me a rough fix" request.
 *
 * Only checks/reads location here — *requesting* the runtime permission
 * itself requires an Activity and happens in WeatherRoute.kt; this class is
 * called only after that permission has already been granted.
 */
class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val locationManager: LocationManager
        get() = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission") // caller must check hasLocationPermission() first
    override suspend fun getCurrentLocation(): Coordinates {
        if (!hasLocationPermission()) {
            throw WeatherError.LocationPermissionDenied
        }

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> throw WeatherError.LocationUnavailable
        }

        val coordinates = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (continuation.isActive) {
                            continuation.resume(Coordinates(location.latitude, location.longitude))
                        }
                    }
                }

                continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }

                // Passing an explicit Looper makes this safe to call from a
                // background dispatcher, not just the main thread.
                locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            }
        }

        return coordinates ?: throw WeatherError.LocationUnavailable
    }

    private companion object {
        const val LOCATION_TIMEOUT_MS = 10_000L
    }
}
