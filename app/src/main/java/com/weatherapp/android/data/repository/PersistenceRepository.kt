package com.weatherapp.android.data.repository

/**
 * Everything the app needs to remember between launches. Currently just the
 * last searched city, but kept behind an interface so it's mockable in
 * ViewModel tests and a one-line change if it grows.
 */
interface PersistenceRepository {
    var lastSearchedCity: String?
}
