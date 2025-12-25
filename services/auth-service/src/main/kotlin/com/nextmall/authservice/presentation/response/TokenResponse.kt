package com.nextmall.authservice.presentation.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
