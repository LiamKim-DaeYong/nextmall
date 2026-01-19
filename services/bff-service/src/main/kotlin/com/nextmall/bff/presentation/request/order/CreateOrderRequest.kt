package com.nextmall.bff.presentation.request.order

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class CreateOrderRequest(
    @field:Positive(message = "userId must be positive")
    val userId: Long,
    @field:Positive(message = "productId must be positive")
    val productId: Long,
    @field:Min(value = 1, message = "quantity must be at least 1")
    val quantity: Int,
)
