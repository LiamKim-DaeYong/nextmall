package com.nextmall.bff.presentation.response.user

import com.nextmall.bff.application.user.query.UserViewResult

data class UserViewResponse(
    val id: Long,
    val nickname: String,
    val email: String?,
)

fun UserViewResult.toResponse() =
    UserViewResponse(
        id = id,
        nickname = nickname,
        email = email,
    )
