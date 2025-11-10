package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
) {
    fun login(email: String, password: String): TokenResponse {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid email or password")

        require(passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        val accessToken = tokenProvider.generateAccessToken(user.id.toString())
        val refreshToken = tokenProvider.generateRefreshToken(user.id.toString())

        return TokenResponse(accessToken, refreshToken)
    }
}
