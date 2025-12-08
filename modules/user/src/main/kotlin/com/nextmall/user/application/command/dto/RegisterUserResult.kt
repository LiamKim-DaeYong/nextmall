package com.nextmall.user.application.command.dto

import com.nextmall.common.util.toUtc
import com.nextmall.user.domain.entity.User
import java.time.OffsetDateTime

data class RegisterUserResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun from(user: User): RegisterUserResult =
            RegisterUserResult(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                createdAt = user.createdAt.toUtc(),
            )
    }
}
