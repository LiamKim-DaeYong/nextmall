package com.nextmall.user.application.command.create

import com.nextmall.user.domain.entity.User

data class CreateUserResult(
    val id: Long,
    val nickname: String,
    val email: String?,
) {
    companion object {
        fun from(user: User): CreateUserResult =
            CreateUserResult(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
            )
    }
}
