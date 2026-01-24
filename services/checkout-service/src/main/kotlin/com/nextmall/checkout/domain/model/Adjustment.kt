package com.nextmall.checkout.domain.model

data class Adjustment(
    val type: String,
    val amount: Money,
)
