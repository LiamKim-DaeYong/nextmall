package com.nextmall.bff.presentation.response.order

import com.nextmall.bff.application.order.query.OrderViewResult
import com.nextmall.common.util.Money

data class OrderViewResponse(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    val totalPrice: Money,
    val status: String,
)

fun OrderViewResult.toResponse() =
    OrderViewResponse(
        id = id,
        userId = userId,
        productId = productId,
        quantity = quantity,
        totalPrice = totalPrice,
        status = status,
    )
