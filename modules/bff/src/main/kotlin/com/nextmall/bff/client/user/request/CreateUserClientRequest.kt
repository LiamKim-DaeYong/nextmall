package com.nextmall.bff.client.user.request

data class CreateUserClientRequest(
    val nickname: String,
    val email: String?,
)
