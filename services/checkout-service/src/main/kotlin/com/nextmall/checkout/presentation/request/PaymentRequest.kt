package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.PaymentCommand
import com.nextmall.checkout.application.command.PaymentHandlerCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class PaymentHandlerRequest(
    @field:NotBlank
    val type: String,
    val provider: String?,
)

data class PaymentRequest(
    @field:NotEmpty
    @field:Valid
    val handlers: List<PaymentHandlerRequest>,
)

fun PaymentRequest.toCommand(): PaymentCommand =
    PaymentCommand(
        handlers = handlers.map { it.toCommand() },
    )

fun PaymentHandlerRequest.toCommand(): PaymentHandlerCommand =
    PaymentHandlerCommand(
        type = type,
        provider = provider,
    )
