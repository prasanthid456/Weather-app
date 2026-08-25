package com.weatherapp.android.domain

// every failure in the data layer gets mapped to one of these so the UI
// only ever has to show userMessage, never a raw exception or HTTP code
sealed class WeatherError(val userMessage: String) : Exception(userMessage) {

    data class CityNotFound(val city: String?) : WeatherError(
        if (!city.isNullOrBlank()) {
            "We couldn't find \"$city\". Check the spelling and try again."
        } else {
            "Enter a city name to search."
        }
    )

    data object NoConnectivity :
        WeatherError("You appear to be offline. Check your connection and try again.")

    data object InvalidApiKey :
        WeatherError("The weather service rejected our request. Please try again later.")

    data object LocationPermissionDenied :
        WeatherError("Location access is off. Enable it in Settings, or search for a city instead.")

    data object LocationUnavailable :
        WeatherError("We couldn't determine your current location. Try searching for a city instead.")

    data class Server(val statusCode: Int) :
        WeatherError("The weather service returned an error ($statusCode). Please try again later.")

    data object Unknown : WeatherError("Something went wrong. Please try again.")
}
