package com.nextmall.bff.application.auth.login

import com.nextmall.bff.application.auth.token.TokenResult
import com.nextmall.bff.client.auth.AuthServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class LoginFacadeImpl(
    private val authServiceClient: AuthServiceClient,
) : LoginFacade {
    override fun login(command: LoginCommand): Mono<TokenResult> =
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
