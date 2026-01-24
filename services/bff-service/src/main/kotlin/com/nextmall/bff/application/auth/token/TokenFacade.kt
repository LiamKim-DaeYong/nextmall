package com.nextmall.bff.application.auth.token

import com.nextmall.bff.client.auth.WebClientAuthServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class TokenFacade(
    private val authServiceClient: WebClientAuthServiceClient,
) {
    /**
     * 로그아웃 처리를 인증 서비스에 위임한다.
     */
    fun logout(refreshToken: String): Mono<Void> =
        authServiceClient.logout(refreshToken)

    /**
     * 리프레시 토큰으로 액세스 토큰을 재발급한다.
     */
    fun refresh(refreshToken: String): Mono<TokenResult> =
        authServiceClient
            .refresh(refreshToken)
            .map { token ->
                TokenResult(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                )
            }
}
