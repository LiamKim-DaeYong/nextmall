package com.nextmall.bff.presentation.response.signup

import com.nextmall.bff.application.signup.SignUpResult

data class SignUpResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)

fun SignUpResult.toResponse() =
    SignUpResponse(
        userId = userId,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
