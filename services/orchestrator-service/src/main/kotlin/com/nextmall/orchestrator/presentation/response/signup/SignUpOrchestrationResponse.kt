package com.nextmall.orchestrator.presentation.response.signup

import com.nextmall.orchestrator.application.signup.SignUpResult

data class SignUpOrchestrationResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)

fun SignUpResult.toResponse() =
    SignUpOrchestrationResponse(
        userId = userId,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
