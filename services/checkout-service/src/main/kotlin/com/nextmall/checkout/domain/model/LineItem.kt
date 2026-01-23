package com.nextmall.checkout.domain.model

data class LineItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val price: Money,
    val imageUrl: String?,
)
