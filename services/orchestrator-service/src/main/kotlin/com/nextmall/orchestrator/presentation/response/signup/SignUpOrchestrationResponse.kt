package com.nextmall.orchestrator.presentation.response.signup

import com.nextmall.orchestrator.application.signup.SignUpResult
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class SignUpOrchestrationResponse(
    @JsonSerialize(using = ToStringSerializer::class)
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
