package com.nextmall.bffservice.presentation.request.order

data class CreateOrderRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
