package com.nextmall.bff.client.user.response

data class UserViewClientResult(
    val id: Long,
    val nickname: String,
    val email: String?,
)
