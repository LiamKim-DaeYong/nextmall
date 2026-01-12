package com.nextmall.bff.client.order.request

import com.nextmall.common.util.Money

data class CreateOrderClientRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
)
