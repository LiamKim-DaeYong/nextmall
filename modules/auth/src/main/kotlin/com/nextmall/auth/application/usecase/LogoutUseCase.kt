package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import org.springframework.stereotype.Service

@Service
class LogoutUseCase(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) {
    fun logout(refreshToken: String) {
        runCatching {
            val userId = tokenProvider.parseRefreshToken(refreshToken)
            refreshTokenStore.delete(userId)
        }
    }
}
