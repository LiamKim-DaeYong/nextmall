package com.nextmall.bff.client.orchestrator.request

import com.nextmall.bff.client.auth.AuthProvider

data class SignUpOrchestrationClientRequest(
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String? = null,
    val nickname: String,
)
