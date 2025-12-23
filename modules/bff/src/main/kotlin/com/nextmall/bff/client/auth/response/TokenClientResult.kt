package com.nextmall.bff.client.auth.response

data class TokenClientResult(
    val accessToken: String,
    val refreshToken: String,
)
