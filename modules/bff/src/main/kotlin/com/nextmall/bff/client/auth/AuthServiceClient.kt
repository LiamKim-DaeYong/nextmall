package com.nextmall.bff.client.auth

import com.nextmall.bff.client.auth.response.TokenClientResult

interface AuthServiceClient {
    suspend fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    )

    suspend fun issueToken(
        userId: Long,
    ): TokenClientResult
}
