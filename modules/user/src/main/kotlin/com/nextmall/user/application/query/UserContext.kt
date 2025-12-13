package com.nextmall.user.application.query

import java.time.OffsetDateTime

data class UserContext(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: OffsetDateTime,
)
