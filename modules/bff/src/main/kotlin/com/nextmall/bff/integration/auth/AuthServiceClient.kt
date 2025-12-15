package com.nextmall.bff.integration.auth

import com.nextmall.bff.integration.auth.response.TokenResult

interface AuthServiceClient {
    suspend fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    )

    suspend fun issueToken(
        userId: Long,
    ): TokenResult
}
