package com.nextmall.auth.application

import com.nextmall.auth.application.exception.UnsupportedProviderException
import com.nextmall.auth.application.login.LoginStrategy
import com.nextmall.auth.application.token.TokenResult
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
import com.nextmall.auth.infrastructure.cache.RefreshTokenRepository
import com.nextmall.auth.infrastructure.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthTokenService(
    private val loginStrategies: List<LoginStrategy>,
    private val tokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 로그인: 인증 정보를 검증하고 토큰을 발급한다.
     */
    @Transactional
    fun login(
        provider: AuthProvider,
        identifier: String,
        credential: String?,
    ): TokenResult {
        val strategy =
            loginStrategies.firstOrNull { it.supports(provider) }
                ?: throw UnsupportedProviderException()

        val authAccountId = strategy.authenticate(identifier, credential)

        return issueAndStoreTokens(authAccountId)
    }

    /**
     * 특정 사용자에 대해 토큰만 발급한다.
     * 회원가입 직후 자동 로그인 흐름에서 사용된다.
     */
    @Transactional
    fun issueForAuthAccount(
        authAccountId: Long,
    ): TokenResult =
        issueAndStoreTokens(authAccountId)

    /**
     * RefreshToken을 사용해 토큰을 재발급한다.
     */
    @Transactional
    fun refresh(
        refreshToken: String,
    ): TokenResult {
        val authAccountId =
            refreshTokenRepository.findAuthAccountId(refreshToken)
                ?: throw InvalidRefreshTokenException()

        // 토큰 재사용 방지: 기존 refreshToken은 즉시 폐기
        refreshTokenRepository.delete(refreshToken)

        return issueAndStoreTokens(authAccountId)
    }

    /**
     * RefreshToken을 무효화한다. (로그아웃)
     */
    @Transactional
    fun revoke(
        refreshToken: String,
    ) {
        try {
            refreshTokenRepository.delete(refreshToken)
        } catch (e: Exception) {
            // 로깅만 하고 Exception 흡수
            logger.warn("Failed to revoke refresh token (ignored): {}", e.message)
        }
    }

    private fun issueAndStoreTokens(
        authAccountId: Long,
    ): TokenResult {
        val accessToken =
            tokenProvider.generateAccessToken(
                authAccountId = authAccountId,
                roles = emptyList(),
            )


        val refreshToken =
            tokenProvider.generateRefreshToken(
                authAccountId = authAccountId,
            )

        refreshTokenRepository.save(
            refreshToken = refreshToken,
            authAccountId = authAccountId,
            ttlSeconds = tokenProvider.refreshTokenTtlSeconds(),
        )

        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
