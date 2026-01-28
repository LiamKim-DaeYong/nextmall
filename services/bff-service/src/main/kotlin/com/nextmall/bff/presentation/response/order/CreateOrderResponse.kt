package com.nextmall.bff.presentation.response.order

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateOrderResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val orderId: Long,
)
