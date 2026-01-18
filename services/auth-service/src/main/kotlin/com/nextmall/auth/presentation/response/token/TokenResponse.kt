package com.nextmall.auth.presentation.response.token

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
