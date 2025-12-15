package com.nextmall.auth.presentation.public.mapper

import com.nextmall.auth.application.command.token.TokenResult
import com.nextmall.auth.presentation.public.response.TokenResponse
import org.springframework.stereotype.Component

@Component
class AuthResponseMapper {
    fun toTokenResponse(result: TokenResult) =
        TokenResponse(result.accessToken, result.refreshToken)
}
