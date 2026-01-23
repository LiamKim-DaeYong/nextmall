package com.nextmall.checkout.application.command

data class AddressCommand(
    val line1: String?,
    val line2: String?,
    val city: String?,
    val region: String?,
    val postalCode: String?,
    val country: String?,
)
