package com.nextmall.bff.signup.application.command

import com.nextmall.auth.domain.model.AuthProvider

data class SignUpCommandParam(
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String? = null,
    val nickname: String,
)
