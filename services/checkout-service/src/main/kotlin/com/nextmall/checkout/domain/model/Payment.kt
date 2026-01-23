package com.nextmall.checkout.domain.model

data class PaymentHandler(
    val type: String,
    val provider: String?,
)

data class Payment(
    val handlers: List<PaymentHandler>,
)
