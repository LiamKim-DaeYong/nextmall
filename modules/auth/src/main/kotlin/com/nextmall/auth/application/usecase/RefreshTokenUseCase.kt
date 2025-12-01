package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidLoginException
import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class RefreshTokenUseCase(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
    private val userRepository: UserRepository,
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
            userRepository
                .findById(userId)
                .getOrElse { throw InvalidLoginException() }

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
