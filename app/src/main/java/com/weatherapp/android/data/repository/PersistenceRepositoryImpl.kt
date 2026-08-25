package com.weatherapp.android.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PersistenceRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : PersistenceRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override var lastSearchedCity: String?
        get() = prefs.getString(KEY_LAST_CITY, null)
        set(value) {
            prefs.edit().putString(KEY_LAST_CITY, value).apply()
        }

    private companion object {
        const val PREFS_NAME = "weather_app_prefs"
        const val KEY_LAST_CITY = "last_searched_city"
    }
}
