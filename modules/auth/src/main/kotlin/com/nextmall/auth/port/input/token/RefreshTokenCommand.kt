package com.nextmall.auth.port.input.token

import com.nextmall.auth.application.command.token.TokenResult

interface RefreshTokenCommand {
    fun refreshToken(refreshToken: String): TokenResult
}
