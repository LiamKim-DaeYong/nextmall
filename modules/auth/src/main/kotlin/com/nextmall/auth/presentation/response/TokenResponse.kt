package com.nextmall.auth.presentation.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
