package com.nextmall.order.presentation.dto

data class OrderTotals(
    val subtotal: MoneyAmount,
    val tax: MoneyAmount,
    val shipping: MoneyAmount,
    val discount: MoneyAmount,
    val total: MoneyAmount,
)
