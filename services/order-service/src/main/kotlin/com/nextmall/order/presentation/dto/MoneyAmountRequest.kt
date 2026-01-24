package com.nextmall.order.presentation.dto

import jakarta.validation.constraints.NotBlank

data class MoneyAmountRequest(
    val amount: Long,

    @field:NotBlank
    val currency: String,
)
