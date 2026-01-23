package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.AddressCommand

data class AddressRequest(
    val line1: String?,
    val line2: String?,
    val city: String?,
    val region: String?,
    val postalCode: String?,
    val country: String?,
)

fun AddressRequest.toCommand(): AddressCommand =
    AddressCommand(
        line1 = line1,
        line2 = line2,
        city = city,
        region = region,
        postalCode = postalCode,
        country = country,
    )
