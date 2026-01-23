package com.nextmall.checkout.application.command

data class PaymentHandlerCommand(
    val type: String,
    val provider: String?,
)

data class PaymentCommand(
    val handlers: List<PaymentHandlerCommand>,
)
