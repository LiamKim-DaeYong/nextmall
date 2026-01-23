package com.nextmall.order.presentation.dto

data class OrderLineItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val price: MoneyAmount,
    val imageUrl: String? = null,
)

