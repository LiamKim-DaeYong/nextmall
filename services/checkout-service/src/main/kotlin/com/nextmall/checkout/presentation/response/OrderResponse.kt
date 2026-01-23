package com.nextmall.checkout.presentation.response

data class OrderResponse(
    val id: String,
    val checkoutId: String,
    val permalinkUrl: String,
    val lineItems: List<LineItemResponse>,
    val totals: TotalsResponse,
)
