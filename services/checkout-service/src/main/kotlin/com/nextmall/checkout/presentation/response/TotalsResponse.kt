package com.nextmall.checkout.presentation.response

data class TotalsResponse(
    val subtotal: MoneyResponse,
    val tax: MoneyResponse,
    val shipping: MoneyResponse,
    val discount: MoneyResponse,
    val total: MoneyResponse,
)
