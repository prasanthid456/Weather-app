package com.weatherapp.android.di

import com.weatherapp.android.data.cache.WeatherIconCache
import com.weatherapp.android.data.cache.WeatherIconCacheImpl
import com.weatherapp.android.data.location.LocationProvider
import com.weatherapp.android.data.location.LocationProviderImpl
import com.weatherapp.android.data.repository.PersistenceRepository
import com.weatherapp.android.data.repository.PersistenceRepositoryImpl
import com.weatherapp.android.data.repository.WeatherRepository
import com.weatherapp.android.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Binds each service to its interface so nothing outside this file (and
// NetworkModule) ever references a concrete *Impl - lets tests swap in
// fakes without dragging in Hilt/Dagger.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindPersistenceRepository(impl: PersistenceRepositoryImpl): PersistenceRepository

    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: LocationProviderImpl): LocationProvider

    @Binds
    @Singleton
    abstract fun bindWeatherIconCache(impl: WeatherIconCacheImpl): WeatherIconCache
}
