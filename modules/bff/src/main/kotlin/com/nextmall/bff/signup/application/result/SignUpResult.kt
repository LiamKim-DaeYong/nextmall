package com.nextmall.bff.signup.application.result

data class SignUpResult(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
