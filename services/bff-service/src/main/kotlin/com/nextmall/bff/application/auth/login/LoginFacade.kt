package com.nextmall.bff.application.auth.login

import com.nextmall.bff.application.auth.token.TokenResult
import com.nextmall.bff.client.auth.AuthServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoginFacade(
    private val authServiceClient: AuthServiceClient,
) {
    /**
     * 로그인 요청을 인증 서비스에 위임한다.
     */
    fun login(command: LoginCommand): Mono<TokenResult> =
        authServiceClient
            .login(
                provider = command.provider,
                principal = command.principal,
                credential = command.credential,
            ).map { token ->
                TokenResult(
                    accessToken = token.accessToken,
                    refreshToken = token.refreshToken,
                )
            }
}
