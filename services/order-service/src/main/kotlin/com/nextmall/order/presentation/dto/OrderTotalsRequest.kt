package com.nextmall.order.presentation.dto

import jakarta.validation.Valid

data class OrderTotalsRequest(
    @field:Valid
    val subtotal: MoneyAmountRequest,

    @field:Valid
    val tax: MoneyAmountRequest,

    @field:Valid
    val shipping: MoneyAmountRequest,

    @field:Valid
    val discount: MoneyAmountRequest,

    @field:Valid
    val total: MoneyAmountRequest,
)

