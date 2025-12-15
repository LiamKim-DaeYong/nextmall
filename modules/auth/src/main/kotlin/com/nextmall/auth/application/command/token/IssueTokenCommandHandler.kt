package com.nextmall.auth.application.command.token

import com.nextmall.auth.port.input.token.IssueTokenCommand
import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IssueTokenCommandHandler(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) : IssueTokenCommand {
    @Transactional
    override fun issue(userId: Long): TokenResult {
        // 1. 토큰 생성
        val accessToken =
            tokenProvider.generateAccessToken(userId)

        val refreshToken =
            tokenProvider.generateRefreshToken(userId)

        // 2. RefreshToken 저장 (Redis)
        refreshTokenStore.save(
            userId = userId,
            refreshToken = refreshToken,
            ttlSeconds = tokenProvider.refreshTokenTtlSeconds(),
        )

        // 3. 결과 반환
        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
