package com.nextmall.bff.client.order.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateOrderClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val orderId: Long,
)
