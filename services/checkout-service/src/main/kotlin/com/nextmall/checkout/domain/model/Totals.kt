package com.nextmall.checkout.domain.model

data class Totals(
    val subtotal: Money,
    val tax: Money,
    val shipping: Money,
    val discount: Money,
    val total: Money,
)
