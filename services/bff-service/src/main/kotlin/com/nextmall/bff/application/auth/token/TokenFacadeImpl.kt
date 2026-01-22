package com.nextmall.bff.application.auth.token

import com.nextmall.bff.client.auth.WebClientAuthServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class TokenFacadeImpl(
    private val authServiceClient: WebClientAuthServiceClient,
) : TokenFacade {
    override fun logout(refreshToken: String): Mono<Void> =
        authServiceClient.logout(refreshToken)

    override fun refresh(refreshToken: String): Mono<TokenResult> =
        authServiceClient
            .refresh(refreshToken)
            .map { token ->
                TokenResult(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                )
            }
}
