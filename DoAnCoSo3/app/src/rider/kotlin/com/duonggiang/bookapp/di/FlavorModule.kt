package com.duonggiang.bookapp.di

import com.duonggiang.bookapp.data.SocketService
import com.duonggiang.bookapp.data.repository.LocationUpdateSocketRepository
import com.duonggiang.bookapp.location.LocationManager
import com.duonggiang.bookapp.ui.features.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule{
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
        locationManager: LocationManager
    ): LocationUpdateBaseRepository {
        return LocationUpdateSocketRepository(socketService, locationManager)
    }
}