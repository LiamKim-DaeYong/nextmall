package com.nextmall.orchestrator.application.signup

import com.nextmall.orchestrator.client.auth.AuthProvider

data class SignUpCommand(
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String? = null,
    val nickname: String,
)
