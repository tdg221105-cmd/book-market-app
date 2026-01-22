package com.duonggiang.bookapp.data.models

data class CheckoutDetails(
    val deliveryFee: Double,
    val subTotal: Double,
    val tax: Double,
    val totalAmount: Double
)