package com.weatherapp.android.fakes

import android.graphics.Bitmap
import com.weatherapp.android.data.cache.WeatherIconCache
import com.weatherapp.android.domain.WeatherError

class FakeWeatherIconCache : WeatherIconCache {
    var bitmapToReturn: Bitmap? = null

    override suspend fun loadIcon(url: String): Bitmap = bitmapToReturn ?: throw WeatherError.Unknown
}
