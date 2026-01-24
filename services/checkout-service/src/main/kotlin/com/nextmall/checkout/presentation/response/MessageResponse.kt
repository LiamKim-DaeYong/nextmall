package com.nextmall.checkout.presentation.response

data class MessageResponse(
    val code: String,
    val message: String?,
    val severity: String,
)
