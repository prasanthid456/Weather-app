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

// Using plain android.location instead of Play Services' FusedLocationProviderClient
// so there's no Play Services dependency just for a one-shot rough location fix.
// Doesn't request the permission itself - that has to happen in an Activity
// (WeatherRoute), this only runs once permission is already granted.
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

                // explicit Looper so this works when called off the main thread
                locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            }
        }

        return coordinates ?: throw WeatherError.LocationUnavailable
    }

    private companion object {
        const val LOCATION_TIMEOUT_MS = 10_000L
    }
}
