package com.nextmall.orchestrator.application.signup

data class SignUpResult(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
