package com.nextmall.orchestrator.client.auth

import com.nextmall.orchestrator.client.auth.response.TokenClientResponse

interface AuthServiceClient {
    fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Long

    fun issueToken(authAccountId: Long): TokenClientResponse
}
