package com.nextmall.user.presentation.dto.response

import com.nextmall.common.util.TimeUtils.DEFAULT_ZONE
import com.nextmall.user.domain.model.User
import java.time.OffsetDateTime

data class RegisterUserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun from(user: User): RegisterUserResponse =
            RegisterUserResponse(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                createdAt =
                    user.createdAt?.atOffset(DEFAULT_ZONE)
                        ?: OffsetDateTime.now(DEFAULT_ZONE),
            )
    }
}
