package com.nextmall.bff.application.auth.login

import com.nextmall.bff.application.auth.token.TokenResult
import com.nextmall.bff.client.auth.AuthServiceClient
import org.springframework.stereotype.Component

@Component
class LoginFacadeImpl(
    private val authServiceClient: AuthServiceClient,
) : LoginFacade {

    override suspend fun login(command: LoginCommand): TokenResult {
        val token =
            authServiceClient.login(
                provider = command.provider,
                principal = command.principal,
                credential = command.credential,
            )

        return TokenResult(
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
        )
    }
}
