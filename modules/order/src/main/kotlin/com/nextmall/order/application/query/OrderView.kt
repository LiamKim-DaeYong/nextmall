package com.nextmall.order.application.query

import com.nextmall.common.util.Money
import com.nextmall.order.domain.model.OrderStatus

data class OrderView(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
    val status: OrderStatus
)
