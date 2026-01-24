package com.nextmall.orchestrator.client.user.request

data class CreateUserClientRequest(
    val nickname: String,
    val email: String?,
)
