package com.nextmall.bff.client.order.request

data class CreateOrderClientRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
