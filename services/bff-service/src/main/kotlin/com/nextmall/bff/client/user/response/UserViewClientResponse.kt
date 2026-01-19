package com.nextmall.bff.client.user.response

data class UserViewClientResponse(
    val id: Long,
    val nickname: String,
    val email: String?,
)
