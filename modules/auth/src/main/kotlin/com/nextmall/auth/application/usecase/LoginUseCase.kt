package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidLoginException
import com.nextmall.auth.domain.exception.TooManyLoginAttemptsException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.infrastructure.redis.RateLimitRepository
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val rateLimitRepository: RateLimitRepository,
) {
    fun login(email: String, password: String): TokenResponse {
        val failCount = rateLimitRepository.getFailCount(email)
        if (failCount >= 5) {
            throw TooManyLoginAttemptsException()
        }

        val user =
            userRepository.findByEmail(email)
                ?: run {
                    rateLimitRepository.increaseFailCount(email)
                    throw InvalidLoginException()
                }

        if (!passwordEncoder.matches(password, user.password)) {
            rateLimitRepository.increaseFailCount(email)
            throw InvalidLoginException()
        }

        rateLimitRepository.resetFailCount(email)

        val userId = user.id.toString()
        return TokenResponse(
            accessToken = tokenProvider.generateAccessToken(userId),
            refreshToken = tokenProvider.generateRefreshToken(userId),
        )
    }
}
