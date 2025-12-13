package com.nextmall.auth.application.command.account

import com.nextmall.auth.domain.model.AuthProvider

data class CreateAuthUserAccountCommandParam(
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String?,
)
