package com.weatherapp.android.data.cache

import android.graphics.Bitmap

interface WeatherIconCache {
    suspend fun loadIcon(url: String): Bitmap
}
