package com.nextmall.user.application.query.dto

data class UserCredentials(
    val id: Long,
    val password: String?,
    val role: String,
)
