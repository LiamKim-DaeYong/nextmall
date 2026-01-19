package com.nextmall.bff.application.signup

data class SignUpResult(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
