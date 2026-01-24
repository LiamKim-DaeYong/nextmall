package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.MoneyCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class MoneyRequest(
    @field:NotNull
    @field:PositiveOrZero
    val amount: Long,
    @field:NotBlank
    val currency: String,
)

fun MoneyRequest.toCommand(): MoneyCommand =
    MoneyCommand(
        amount = amount,
        currency = currency,
    )
