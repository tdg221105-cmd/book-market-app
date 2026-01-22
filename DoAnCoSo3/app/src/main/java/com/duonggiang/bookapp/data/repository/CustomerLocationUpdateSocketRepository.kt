package com.duonggiang.bookapp.data.repository

import com.duonggiang.bookapp.data.SocketService
import com.duonggiang.bookapp.ui.features.orders.LocationUpdateBaseRepository
import javax.inject.Inject

class CustomerLocationUpdateSocketRepository @Inject constructor(socketService: SocketService) :
    LocationUpdateBaseRepository(socketService) {

    override fun connect(orderID: String, riderID: String) {
        try {
            socketService.connect(
                orderID, riderID, null, null
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun disconnect() {
        socketService.disconnect()
    }
}