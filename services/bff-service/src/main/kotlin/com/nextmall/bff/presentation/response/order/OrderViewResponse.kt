package com.nextmall.bff.presentation.response.order

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer
import com.nextmall.bff.application.order.query.OrderViewResult
import com.nextmall.common.util.Money

data class OrderViewResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    @JsonSerialize(using = ToStringSerializer::class)
    val userId: Long,
    @JsonSerialize(using = ToStringSerializer::class)
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
