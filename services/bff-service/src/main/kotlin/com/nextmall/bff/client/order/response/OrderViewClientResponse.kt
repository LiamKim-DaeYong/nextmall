package com.nextmall.bff.client.order.response

import com.nextmall.common.util.Money

data class OrderViewClientResponse(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
    val status: String,
)
