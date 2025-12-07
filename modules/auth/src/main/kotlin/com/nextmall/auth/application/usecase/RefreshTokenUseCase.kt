package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidLoginException
import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.user.domain.repository.UserCredentialsQueryRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCase(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
    private val credentialsRepository: UserCredentialsQueryRepository,
) {
    fun refresh(refreshToken: String): TokenResponse {
        val userId = tokenProvider.parseRefreshToken(refreshToken)

        val savedToken =
            refreshTokenStore.findByUserId(userId)
                ?: throw InvalidRefreshTokenException()

        if (savedToken != refreshToken) {
            throw InvalidRefreshTokenException()
        }

        val user =
            credentialsRepository.findById(userId)
                ?: throw InvalidLoginException()

        val newAccessToken = tokenProvider.generateAccessToken(userId.toString(), user.role)
        val newRefreshToken = tokenProvider.generateRefreshToken(userId.toString())

        val ttl = tokenProvider.refreshTokenTtlSeconds()
        refreshTokenStore.save(userId, newRefreshToken, ttl)

        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
