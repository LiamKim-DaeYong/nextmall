package com.nextmall.bff.application.user.query

data class UserViewResult(
    val id: Long,
    val nickname: String,
    val email: String?,
)
