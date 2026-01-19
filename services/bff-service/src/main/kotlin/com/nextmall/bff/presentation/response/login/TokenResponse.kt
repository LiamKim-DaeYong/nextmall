package com.nextmall.bff.presentation.response.login

import com.nextmall.bff.application.auth.token.TokenResult

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

fun TokenResult.toResponse() =
    TokenResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
