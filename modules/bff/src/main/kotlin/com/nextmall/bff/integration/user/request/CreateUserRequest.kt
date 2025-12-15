package com.nextmall.bff.integration.user.request

data class CreateUserRequest(
    val nickname: String,
    val email: String?,
)
