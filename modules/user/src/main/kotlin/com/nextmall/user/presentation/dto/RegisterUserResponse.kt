package com.nextmall.user.presentation.dto

import com.nextmall.common.util.TimeUtils.DEFAULT_ZONE
import com.nextmall.user.domain.model.User
import java.time.OffsetDateTime

data class RegisterUserResponse(
    val userId: Long,
    val email: String,
    val createdAt: OffsetDateTime,
) {
    companion object {
        fun from(user: User): RegisterUserResponse =
            RegisterUserResponse(
                userId = requireNotNull(user.id) { "User must be persisted before creating response" },
                email = user.email,
                createdAt =
                    user.createdAt?.atOffset(DEFAULT_ZONE)
                        ?: OffsetDateTime.now(DEFAULT_ZONE),
            )
    }
}
