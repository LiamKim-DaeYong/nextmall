package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.BuyerCommand

data class BuyerRequest(
    val id: String?,
    val email: String?,
    val name: String?,
)

fun BuyerRequest.toCommand(): BuyerCommand =
    BuyerCommand(
        id = id,
        email = email,
        name = name,
    )
