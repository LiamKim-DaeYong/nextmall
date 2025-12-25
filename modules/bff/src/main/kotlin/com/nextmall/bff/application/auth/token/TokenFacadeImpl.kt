package com.nextmall.bff.application.auth.token

import com.nextmall.bff.client.auth.WebClientAuthServiceClient
import org.springframework.stereotype.Component

@Component
class TokenFacadeImpl(
    private val authServiceClient: WebClientAuthServiceClient,
) : TokenFacade {
    override suspend fun logout(refreshToken: String) {
        authServiceClient.logout(refreshToken)
    }

    override suspend fun refresh(refreshToken: String): TokenResult {
        val token = authServiceClient.refresh(refreshToken)

        return TokenResult(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
        )
    }
}
