package com.nextmall.auth.application.command.token

import com.nextmall.auth.domain.exception.token.InvalidRefreshTokenException
import com.nextmall.auth.port.input.token.RefreshTokenCommand
import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import org.springframework.stereotype.Service

@Service
class RefreshTokenCommandHandler(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) : RefreshTokenCommand {
    override fun refreshToken(refreshToken: String): TokenResult {
        val userId = tokenProvider.parseRefreshToken(refreshToken)

        val savedToken =
            refreshTokenStore.findByUserId(userId)
                ?: throw InvalidRefreshTokenException()

        if (savedToken != refreshToken) {
            throw InvalidRefreshTokenException()
        }

        val newAccessToken = tokenProvider.generateAccessToken(userId)
        val newRefreshToken = tokenProvider.generateRefreshToken(userId)

        refreshTokenStore.save(
            userId = userId,
            refreshToken = newRefreshToken,
            ttlSeconds = tokenProvider.refreshTokenTtlSeconds(),
        )

        return TokenResult(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
