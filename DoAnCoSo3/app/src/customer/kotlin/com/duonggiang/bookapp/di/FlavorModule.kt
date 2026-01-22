package com.duonggiang.bookapp.di

import com.duonggiang.bookapp.data.SocketService
import com.duonggiang.bookapp.data.repository.CustomerLocationUpdateSocketRepository
import com.duonggiang.bookapp.ui.features.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
    ): LocationUpdateBaseRepository {
        return CustomerLocationUpdateSocketRepository(socketService)
    }
}

