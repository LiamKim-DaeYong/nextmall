package com.nextmall.bff.application.signup

import com.nextmall.bff.client.auth.AuthProvider

data class SignUpCommand(
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String? = null,
    val nickname: String,
)
