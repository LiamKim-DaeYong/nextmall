package com.nextmall.user.application.query

import java.time.OffsetDateTime

data class UserContext(
    val id: Long,
    val nickname: String,
    val email: String?,
    val createdAt: OffsetDateTime,
)
