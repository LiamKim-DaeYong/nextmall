package com.nextmall.user.presentation.response

import com.nextmall.user.application.query.UserView

data class UserViewResponse(
    val id: Long,
    val nickname: String,
    val email: String? = null,
)

fun UserView.toResponse() =
    UserViewResponse(
        id = id,
        nickname = nickname,
        email = email,
    )
