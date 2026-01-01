package com.nextmall.bff.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextAuthTokenProvider : AuthTokenProvider {

    override fun currentToken(): String? =
        SecurityContextHolder
            .getContext()
            .authentication
            ?.credentials as? String
}
