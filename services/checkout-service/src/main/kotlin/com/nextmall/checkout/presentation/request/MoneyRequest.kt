package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.MoneyCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MoneyRequest(
    @field:NotNull
    val amount: Long,
    @field:NotBlank
    val currency: String,
)

fun MoneyRequest.toCommand(): MoneyCommand =
    MoneyCommand(
        amount = amount,
        currency = currency,
    )
