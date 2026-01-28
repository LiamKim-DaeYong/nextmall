package com.nextmall.bff.client.order.response

import com.nextmall.common.util.Money
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class OrderViewClientResponse(
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
