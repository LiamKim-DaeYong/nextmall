package com.nextmall.user.application.command.create

import com.nextmall.user.domain.entity.User

data class CreateUserResult(
    val id: Long,
    val nickname: String,
    val email: String?,
) {
    constructor(user: User) : this(
        id = user.id,
        nickname = user.nickname,
        email = user.email,
    )
}
