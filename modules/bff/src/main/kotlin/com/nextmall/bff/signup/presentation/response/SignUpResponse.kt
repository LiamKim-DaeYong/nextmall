package com.nextmall.bff.signup.presentation.response

data class SignUpResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
