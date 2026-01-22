package com.nextmall.user.presentation.response

import com.nextmall.user.application.query.UserView

data class UserViewResponse(
    val id: String,
    val nickname: String,
    val email: String? = null,
)

fun UserView.toResponse() =
    UserViewResponse(
        id = id.toString(),
        nickname = nickname,
        email = email,
    )
