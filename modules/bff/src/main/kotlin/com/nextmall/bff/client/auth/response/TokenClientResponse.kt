package com.nextmall.bff.client.auth.response

data class TokenClientResponse(
    val accessToken: String,
    val refreshToken: String,
)
