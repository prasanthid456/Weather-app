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

// just one destination for now, but having Navigation Compose wired up
// means adding a second screen later (saved cities, settings, whatever) is
// just another composable() block here instead of a rewrite
@Composable
fun WeatherNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = WeatherDestinations.WEATHER) {
        composable(WeatherDestinations.WEATHER) {
            WeatherRoute()
        }
    }
}
