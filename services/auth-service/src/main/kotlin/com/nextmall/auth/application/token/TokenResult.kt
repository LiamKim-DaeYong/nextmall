package com.nextmall.auth.application.token

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
