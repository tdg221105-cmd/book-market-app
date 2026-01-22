package com.duonggiang.bookapp.data.models

data class CartResponse(
    val checkoutDetails: CheckoutDetails,
    val items: List<CartItem>
)