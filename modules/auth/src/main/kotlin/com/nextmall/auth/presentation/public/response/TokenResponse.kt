package com.nextmall.auth.presentation.public.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
