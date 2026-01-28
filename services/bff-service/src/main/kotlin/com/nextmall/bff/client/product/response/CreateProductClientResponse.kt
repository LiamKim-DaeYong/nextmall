package com.nextmall.bff.client.product.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateProductClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val productId: Long,
)
