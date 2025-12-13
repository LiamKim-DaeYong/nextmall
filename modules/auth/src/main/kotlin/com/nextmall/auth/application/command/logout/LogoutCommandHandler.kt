package com.nextmall.auth.application.command.logout

import com.nextmall.auth.port.input.logout.LogoutCommand
import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LogoutCommandHandler(
    private val tokenProvider: TokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
) : LogoutCommand {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun logout(refreshToken: String) {
        runCatching {
            val userId = tokenProvider.parseRefreshToken(refreshToken)
            refreshTokenStore.delete(userId)
            logger.debug("Logout completed: userId={}", userId)
        }.onFailure { e ->
            logger.debug("Logout operation failed (gracefully handled): ${e.message}")
        }
    }
}
