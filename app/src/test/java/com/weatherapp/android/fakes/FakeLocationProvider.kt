package com.weatherapp.android.fakes

import com.weatherapp.android.data.location.LocationProvider
import com.weatherapp.android.domain.Coordinates
import com.weatherapp.android.domain.WeatherError

class FakeLocationProvider : LocationProvider {
    var permissionGranted: Boolean = false
    var locationResult: Result<Coordinates> = Result.failure(WeatherError.LocationUnavailable)

    override fun hasLocationPermission(): Boolean = permissionGranted

    override suspend fun getCurrentLocation(): Coordinates = locationResult.getOrThrow()
}
