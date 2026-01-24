package com.nextmall.orchestrator.client.auth.response

data class TokenClientResponse(
    val accessToken: String,
    val refreshToken: String,
)
