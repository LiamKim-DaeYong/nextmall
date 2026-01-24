package com.nextmall.orchestrator.client.auth

import com.nextmall.orchestrator.client.auth.response.TokenClientResponse
import reactor.core.publisher.Mono

interface AuthServiceClient {
    fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Mono<Long>

    fun issueToken(authAccountId: Long): Mono<TokenClientResponse>
}
