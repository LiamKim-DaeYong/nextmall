package com.nextmall.checkout.presentation.response

data class LineItemResponse(
    val id: String,
    val title: String,
    val quantity: Int,
    val price: MoneyResponse,
    val imageUrl: String?,
)
