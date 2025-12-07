package com.nextmall.user.presentation.dto.response

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: String,
    val provider: String,
    val createdAt: String,
)
