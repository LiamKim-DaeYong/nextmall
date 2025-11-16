package com.nextmall.auth.presentation.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
