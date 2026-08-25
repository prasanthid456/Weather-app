package com.weatherapp.android.ui.weather

import com.weatherapp.android.Fixtures
import com.weatherapp.android.MainDispatcherRule
import com.weatherapp.android.domain.Coordinates
import com.weatherapp.android.domain.ResolvedLocation
import com.weatherapp.android.domain.WeatherError
import com.weatherapp.android.fakes.FakeLocationProvider
import com.weatherapp.android.fakes.FakePersistenceRepository
import com.weatherapp.android.fakes.FakeWeatherIconCache
import com.weatherapp.android.fakes.FakeWeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var locationProvider: FakeLocationProvider
    private lateinit var persistenceRepository: FakePersistenceRepository
    private lateinit var iconCache: FakeWeatherIconCache
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        weatherRepository = FakeWeatherRepository()
        locationProvider = FakeLocationProvider()
        persistenceRepository = FakePersistenceRepository()
        iconCache = FakeWeatherIconCache()
        viewModel = WeatherViewModel(weatherRepository, locationProvider, persistenceRepository, iconCache)
    }

    @Test
    fun `onAppear with granted permission loads current location weather and persists city`() = runTest {
        locationProvider.permissionGranted = true
        locationProvider.locationResult = Result.success(Coordinates(30.27, -97.74))
        weatherRepository.weatherResult = Result.success(Fixtures.currentWeatherResponse())

        viewModel.onAppear(hasLocationPermission = true)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Loaded)
        assertEquals("Austin", (state as WeatherUiState.Loaded).weather.cityName)
        assertEquals("Austin", persistenceRepository.lastSearchedCity)
    }

    @Test
    fun `onAppear without permission falls back to last searched city`() = runTest {
        persistenceRepository.lastSearchedCity = "Austin"
        weatherRepository.geocodeResult = Result.success(ResolvedLocation(30.27, -97.74, "Austin"))
        weatherRepository.weatherResult = Result.success(Fixtures.currentWeatherResponse())

        viewModel.onAppear(hasLocationPermission = false)

        assertEquals("Austin", weatherRepository.lastRequestedCity)
        assertTrue(viewModel.uiState.value is WeatherUiState.Loaded)
    }

    @Test
    fun `onAppear without permission and no history stays idle`() = runTest {
        viewModel.onAppear(hasLocationPermission = false)

        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `search with valid city updates state and persists resolved city name`() = runTest {
        viewModel.onSearchTextChanged("Austin, TX")
        weatherRepository.geocodeResult = Result.success(ResolvedLocation(30.27, -97.74, "Austin"))
        weatherRepository.weatherResult = Result.success(Fixtures.currentWeatherResponse())

        viewModel.onSearchSubmitted()

        assertTrue(viewModel.uiState.value is WeatherUiState.Loaded)
        assertEquals("Austin", persistenceRepository.lastSearchedCity)
    }

    @Test
    fun `search with unknown city shows friendly error`() = runTest {
        viewModel.onSearchTextChanged("Nowhereville")
        weatherRepository.geocodeResult = Result.failure(WeatherError.CityNotFound("Nowhereville"))

        viewModel.onSearchSubmitted()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Failed)
        assertEquals(WeatherError.CityNotFound("Nowhereville").userMessage, (state as WeatherUiState.Failed).message)
    }

    @Test
    fun `search with blank text does not call repository and fails`() = runTest {
        viewModel.onSearchTextChanged("   ")

        viewModel.onSearchSubmitted()

        assertNull(weatherRepository.lastRequestedCity)
        assertTrue(viewModel.uiState.value is WeatherUiState.Failed)
    }

    @Test
    fun `use current location without permission shows permission error`() = runTest {
        viewModel.onUseCurrentLocationTapped(hasLocationPermission = false)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Failed)
        assertEquals(WeatherError.LocationPermissionDenied.userMessage, (state as WeatherUiState.Failed).message)
    }
}
