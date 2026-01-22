package com.duonggiang.bookapp.data.models

data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String
)
