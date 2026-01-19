package com.nextmall.bff.application.auth.token

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
