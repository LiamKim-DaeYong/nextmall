package com.nextmall.bff.application.order.query

import com.nextmall.common.util.Money

data class OrderViewResult(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
    val status: String,
)
