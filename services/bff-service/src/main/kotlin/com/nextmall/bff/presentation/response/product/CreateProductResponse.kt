package com.nextmall.bff.presentation.response.product

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateProductResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val productId: Long,
)
