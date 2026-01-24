package com.nextmall.order.presentation.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class OrderLineItemRequest(
    @field:NotBlank
    val id: String,

    @field:NotBlank
    val title: String,

    @field:Min(1)
    val quantity: Int,

    @field:Valid
    val price: MoneyAmountRequest,

    val imageUrl: String? = null,
)
