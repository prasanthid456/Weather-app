package com.weatherapp.android.fakes

import com.weatherapp.android.data.repository.PersistenceRepository

class FakePersistenceRepository : PersistenceRepository {
    override var lastSearchedCity: String? = null
}
