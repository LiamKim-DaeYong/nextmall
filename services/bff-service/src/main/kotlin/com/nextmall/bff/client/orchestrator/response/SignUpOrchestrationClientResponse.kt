package com.nextmall.bff.client.orchestrator.response

data class SignUpOrchestrationClientResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
