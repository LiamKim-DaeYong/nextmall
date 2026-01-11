package com.nextmall.orderservice.presentation.response

import com.nextmall.common.util.Money
import com.nextmall.order.application.query.OrderView
import com.nextmall.order.domain.model.OrderStatus

data class OrderViewResponse(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
    val status: OrderStatus
)

fun OrderView.toResponse() =
    OrderViewResponse(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        totalPrice = totalPrice,
        status = status
    )
