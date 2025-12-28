package com.nextmall.authservice.presentation.response.token

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
