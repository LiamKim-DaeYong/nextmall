package com.nextmall.checkout.presentation.response

data class AddressResponse(
    val line1: String?,
    val line2: String?,
    val city: String?,
    val region: String?,
    val postalCode: String?,
    val country: String?,
)
