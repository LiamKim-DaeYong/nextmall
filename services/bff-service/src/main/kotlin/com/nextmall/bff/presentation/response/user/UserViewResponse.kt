package com.nextmall.bff.presentation.response.user

import com.nextmall.bff.application.user.query.UserViewResult

data class UserViewResponse(
    val id: String,
    val nickname: String,
    val email: String?,
)

fun UserViewResult.toResponse() =
    UserViewResponse(
        id = id.toString(),
        nickname = nickname,
        email = email,
    )
