package com.weatherapp.android.ui.weather

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatherapp.android.data.cache.WeatherIconCache
import com.weatherapp.android.data.location.LocationProvider
import com.weatherapp.android.data.remote.model.CurrentWeatherResponse
import com.weatherapp.android.data.repository.PersistenceRepository
import com.weatherapp.android.data.repository.WeatherRepository
import com.weatherapp.android.domain.WeatherError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider,
    private val persistenceRepository: PersistenceRepository,
    private val iconCache: WeatherIconCache
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _iconBitmap = MutableStateFlow<Bitmap?>(null)
    val iconBitmap: StateFlow<Bitmap?> = _iconBitmap.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    // Called once when the screen first appears. WeatherRoute already
    // checked/requested the permission by the time this runs - we just
    // decide what to do with the result. Location wins if granted, else
    // fall back to the last searched city, else show the empty prompt.
    fun onAppear(hasLocationPermission: Boolean) {
        viewModelScope.launch {
            if (hasLocationPermission) {
                loadCurrentLocationWeather()
            } else {
                loadLastSearchedCityIfAvailable()
            }
        }
    }

    fun onSearchSubmitted() {
        viewModelScope.launch { load(_searchText.value) }
    }

    fun onUseCurrentLocationTapped(hasLocationPermission: Boolean) {
        viewModelScope.launch {
            if (hasLocationPermission) {
                loadCurrentLocationWeather()
            } else {
                _uiState.value = WeatherUiState.Failed(WeatherError.LocationPermissionDenied.userMessage)
            }
        }
    }

    private suspend fun loadLastSearchedCityIfAvailable() {
        val lastCity = persistenceRepository.lastSearchedCity
        if (lastCity == null) {
            _uiState.value = WeatherUiState.Idle
            return
        }
        _searchText.value = lastCity
        load(lastCity)
    }

    private suspend fun loadCurrentLocationWeather() {
        _uiState.value = WeatherUiState.Loading
        try {
            val coordinates = locationProvider.getCurrentLocation()
            val response = weatherRepository.getCurrentWeather(coordinates.latitude, coordinates.longitude)
            _searchText.value = response.name
            persistenceRepository.lastSearchedCity = response.name
            present(response)
        } catch (e: Exception) {
            // no fix (denied mid-flow, indoors, emulator w/ no location set) -
            // fall back to last city instead of dead-ending on an error
            val lastCity = persistenceRepository.lastSearchedCity
            if (lastCity != null) {
                load(lastCity)
            } else {
                _uiState.value = WeatherUiState.Failed(messageFor(e))
            }
        }
    }

    private suspend fun load(city: String) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = WeatherUiState.Failed(WeatherError.CityNotFound(null).userMessage)
            return
        }

        _uiState.value = WeatherUiState.Loading
        try {
            val resolved = weatherRepository.geocodeCity(trimmed)
            val response = weatherRepository.getCurrentWeather(resolved.latitude, resolved.longitude)
            persistenceRepository.lastSearchedCity = resolved.resolvedCityName
            present(response)
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Failed(messageFor(e))
        }
    }

    private suspend fun present(response: CurrentWeatherResponse) {
        val model = WeatherUiMapper.map(response)
        _uiState.value = WeatherUiState.Loaded(model)
        _iconBitmap.value = null
        val iconUrl = model.iconUrl
        if (iconUrl != null) {
            _iconBitmap.value = runCatching { iconCache.loadIcon(iconUrl) }.getOrNull()
        }
    }

    private fun messageFor(e: Exception): String =
        (e as? WeatherError)?.userMessage ?: WeatherError.Unknown.userMessage
}
