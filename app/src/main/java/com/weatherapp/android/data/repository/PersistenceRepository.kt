package com.weatherapp.android.data.repository

// just the last searched city for now, behind an interface so it's easy
// to fake in ViewModel tests
interface PersistenceRepository {
    var lastSearchedCity: String?
}
