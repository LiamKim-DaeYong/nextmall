package com.nextmall.auth.application.query.account

import com.nextmall.auth.domain.model.AuthProvider

data class AuthUserAccountContext(
    val id: Long,
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val passwordHash: String?,
)
