package com.weatherapp.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weatherapp.android.ui.weather.WeatherRoute

private object WeatherDestinations {
    const val WEATHER = "weather"
}

/**
 * Single destination today — the brief only calls for one screen. Wiring
 * Navigation Compose in from the start means a second screen (e.g. a
 * saved-cities list) is a new `composable(...)` block here, not a rewrite
 * of how screens get hosted.
 */
@Composable
fun WeatherNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = WeatherDestinations.WEATHER) {
        composable(WeatherDestinations.WEATHER) {
            WeatherRoute()
        }
    }
}
