package com.nextmall.bff.integration.auth.response

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
