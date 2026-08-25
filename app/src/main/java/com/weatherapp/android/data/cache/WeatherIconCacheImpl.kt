package com.weatherapp.android.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import com.weatherapp.android.domain.WeatherError
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

// Two-tier icon cache: in-memory LruCache first, disk folder under
// context.cacheDir behind that so icons don't get re-downloaded every
// launch. Didn't bother with a disk eviction policy - icons are a couple KB
// each and there's only ~50 possible condition codes total.
// Reuses Retrofit's OkHttpClient instead of pulling in Coil/Glide for
// what's really just one GET request and a bitmap decode.
class WeatherIconCacheImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) : WeatherIconCache {

    private val memoryCache = object : LruCache<String, Bitmap>(MEMORY_CACHE_SIZE_KB) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    private val diskCacheDir: File by lazy {
        File(context.cacheDir, "weather_icons").apply { mkdirs() }
    }

    override suspend fun loadIcon(url: String): Bitmap = withContext(Dispatchers.IO) {
        memoryCache.get(url)?.let { return@withContext it }

        val fileName = url.substringAfterLast('/')
        val diskFile = File(diskCacheDir, fileName)
        if (diskFile.exists()) {
            BitmapFactory.decodeFile(diskFile.absolutePath)?.let { bitmap ->
                memoryCache.put(url, bitmap)
                return@withContext bitmap
            }
        }

        val request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).execute().use { response ->
            val bytes = response.body?.bytes() ?: throw WeatherError.Unknown
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw WeatherError.Unknown
            memoryCache.put(url, bitmap)
            // best effort - if this fails we just re-download next time
            runCatching { diskFile.writeBytes(bytes) }
            bitmap
        }
    }

    private companion object {
        const val MEMORY_CACHE_SIZE_KB = 4 * 1024 // 4 MB
    }
}
