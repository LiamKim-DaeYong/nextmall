package com.nextmall.auth.application.command.token

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
