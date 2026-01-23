package com.nextmall.checkout.presentation.response

data class CheckoutSummaryResponse(
    val id: String,
    val status: String,
    val currency: String,
    val totalAmount: Long,
)
