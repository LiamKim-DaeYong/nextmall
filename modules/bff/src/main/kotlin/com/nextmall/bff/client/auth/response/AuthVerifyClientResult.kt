package com.nextmall.bff.client.auth.response

data class AuthVerifyClientResult(
    val authAccountId: Long,
    val userId: Long,
    val roles: List<String>,
)
