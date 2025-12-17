package com.nextmall.auth.application.command.login

import com.nextmall.auth.application.command.token.TokenResult
import com.nextmall.auth.application.command.login.strategy.AuthLoginStrategy
import com.nextmall.auth.domain.exception.common.UnsupportedProviderException
import com.nextmall.auth.port.input.login.LoginCommand
import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import org.springframework.stereotype.Service

@Service
class LoginCommandHandler(
    private val strategies: List<AuthLoginStrategy>,
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) : LoginCommand {
    override fun login(param: LoginCommandParam): TokenResult {
        val strategy =
            strategies.firstOrNull { it.supports(param.provider) }
                ?: throw UnsupportedProviderException()

        val authUserAccountView = strategy.login(param)

        val userId = authUserAccountView.userId
        val accessToken = tokenProvider.generateAccessToken(userId)
        val refreshToken = tokenProvider.generateRefreshToken(userId)

        refreshTokenStore.save(
            userId = userId,
            refreshToken = refreshToken,
            ttlSeconds = tokenProvider.refreshTokenTtlSeconds(),
        )

        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
