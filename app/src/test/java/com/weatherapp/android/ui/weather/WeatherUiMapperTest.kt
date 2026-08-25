package com.weatherapp.android.ui.weather

import com.weatherapp.android.Fixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherUiMapperTest {

    @Test
    fun `map formats fields for display`() {
        val response = Fixtures.currentWeatherResponse()

        val result = WeatherUiMapper.map(response)

        assertEquals("Austin", result.cityName)
        assertEquals("90°F", result.temperature)
        assertEquals("40%", result.humidity)
        assertEquals("Clear Sky", result.conditionDescription)
        assertEquals("https://openweathermap.org/img/wn/01d@2x.png", result.iconUrl)
    }
}
