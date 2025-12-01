package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LogoutUseCase(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun logout(refreshToken: String) {
        runCatching {
            val userId = tokenProvider.parseRefreshToken(refreshToken)
            refreshTokenStore.delete(userId)
            logger.debug("Logout completed: userId={}", userId)
        }.onFailure { e ->
            logger.info("Logout operation failed (gracefully handled): ${e.message}")
        }
    }
}
