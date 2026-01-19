package com.nextmall.bff.client.auth.request

data class RevokeTokenClientRequest(
    val refreshToken: String,
)
