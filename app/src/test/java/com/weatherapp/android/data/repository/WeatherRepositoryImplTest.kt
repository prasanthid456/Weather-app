package com.weatherapp.android.data.repository

import com.weatherapp.android.Fixtures
import com.weatherapp.android.data.remote.OpenWeatherApiService
import com.weatherapp.android.domain.WeatherError
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class WeatherRepositoryImplTest {

    private lateinit var apiService: OpenWeatherApiService
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        apiService = mock()
        repository = WeatherRepositoryImpl(apiService, apiKey = "test-key")
    }

    @Test
    fun `getCurrentWeather returns body on success`() = runTest {
        val fixture = Fixtures.currentWeatherResponse()
        whenever(apiService.getCurrentWeather(30.27, -97.74, "imperial", "test-key"))
            .thenReturn(Response.success(fixture))

        val result = repository.getCurrentWeather(30.27, -97.74)

        assertEquals("Austin", result.name)
    }

    @Test
    fun `getCurrentWeather maps 401 to InvalidApiKey`() = runTest {
        whenever(apiService.getCurrentWeather(any(), any(), any(), any()))
            .thenReturn(Response.error(401, errorBody()))

        try {
            repository.getCurrentWeather(0.0, 0.0)
            fail("Expected InvalidApiKey to be thrown")
        } catch (e: WeatherError.InvalidApiKey) {
            // expected
        }
    }

    @Test
    fun `getCurrentWeather maps IOException to NoConnectivity`() = runTest {
        whenever(apiService.getCurrentWeather(any(), any(), any(), any())).thenAnswer { throw IOException() }

        try {
            repository.getCurrentWeather(0.0, 0.0)
            fail("Expected NoConnectivity to be thrown")
        } catch (e: WeatherError.NoConnectivity) {
            // expected
        }
    }

    @Test
    fun `geocodeCity returns first match on success`() = runTest {
        whenever(apiService.geocodeCity(any(), any(), any())).thenAnswer {
            Response.success(
                listOf(
                    com.weatherapp.android.data.remote.model.GeocodingResult("Austin", 30.27, -97.74, "US", "Texas")
                )
            )
        }

        val result = repository.geocodeCity("Austin, TX")

        assertEquals("Austin", result.resolvedCityName)
        assertEquals(30.27, result.latitude, 0.0)
    }

    @Test
    fun `geocodeCity throws CityNotFound when no matches`() = runTest {
        whenever(apiService.geocodeCity(any(), any(), any())).thenReturn(Response.success(emptyList()))

        try {
            repository.geocodeCity("Nowhereville")
            fail("Expected CityNotFound to be thrown")
        } catch (e: WeatherError.CityNotFound) {
            assertEquals("Nowhereville", e.city)
        }
    }

    private fun errorBody() = "".toResponseBody("text/plain".toMediaTypeOrNull())
}
