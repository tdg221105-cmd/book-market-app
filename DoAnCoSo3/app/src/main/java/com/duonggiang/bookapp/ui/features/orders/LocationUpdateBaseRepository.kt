package com.duonggiang.bookapp.ui.features.orders

import com.duonggiang.bookapp.data.SocketService

abstract class LocationUpdateBaseRepository (val socketService: SocketService)
{
    open val messages = socketService.messages
    abstract fun connect(orderID: String, riderID: String)
    abstract fun disconnect()
}