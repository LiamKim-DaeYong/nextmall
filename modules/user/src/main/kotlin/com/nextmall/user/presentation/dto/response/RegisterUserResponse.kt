package com.nextmall.user.presentation.dto.response

import java.time.OffsetDateTime

data class RegisterUserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: OffsetDateTime,
)
