package com.nextmall.bff.signup.application.command

import com.nextmall.bff.integration.auth.AuthProvider

data class SignUpCommandParam(
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String? = null,
    val nickname: String,
)
