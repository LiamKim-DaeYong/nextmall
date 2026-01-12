package com.nextmall.orderservice.presentation.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CreateOrderRequest(
    @field:NotNull
    val productId: Long,

    @field:NotNull
    @field:Min(1)
    val quantity: Int,
)
