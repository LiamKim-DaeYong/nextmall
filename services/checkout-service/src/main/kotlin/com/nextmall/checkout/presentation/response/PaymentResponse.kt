package com.nextmall.checkout.presentation.response

data class PaymentHandlerResponse(
    val type: String,
    val provider: String?,
)

data class PaymentResponse(
    val handlers: List<PaymentHandlerResponse>,
)
