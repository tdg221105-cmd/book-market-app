package com.duonggiang.bookapp

import android.app.Application
import com.duonggiang.bookapp.notification.FoodHubNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FootHubApp : Application() {

    @Inject
    lateinit var foodHubNotificationManager: FoodHubNotificationManager
    override fun onCreate() {
        super.onCreate()
        foodHubNotificationManager.createChannels()
        foodHubNotificationManager.getAndStoreToken()
    }
}