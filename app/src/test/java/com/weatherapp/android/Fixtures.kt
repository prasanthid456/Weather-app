package com.weatherapp.android

import com.google.gson.Gson
import com.weatherapp.android.data.remote.model.CurrentWeatherResponse

object Fixtures {
    private const val CURRENT_WEATHER_JSON = """
    {
      "coord": { "lon": -97.74, "lat": 30.27 },
      "weather": [{ "id": 800, "main": "Clear", "description": "clear sky", "icon": "01d" }],
      "main": { "temp": 89.6, "feels_like": 91.2, "temp_min": 85.1, "temp_max": 92.3, "pressure": 1012, "humidity": 40 },
      "wind": { "speed": 5.75, "deg": 180 },
      "sys": { "country": "US", "sunrise": 1700000000, "sunset": 1700040000 },
      "name": "Austin",
      "dt": 1700020000
    }
    """

    fun currentWeatherResponse(): CurrentWeatherResponse =
        Gson().fromJson(CURRENT_WEATHER_JSON, CurrentWeatherResponse::class.java)
}
