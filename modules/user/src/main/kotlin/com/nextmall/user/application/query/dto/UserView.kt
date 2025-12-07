package com.nextmall.user.application.query.dto

import java.time.OffsetDateTime

data class UserView(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: String,
    val provider: String,
    val createdAt: OffsetDateTime,
)
