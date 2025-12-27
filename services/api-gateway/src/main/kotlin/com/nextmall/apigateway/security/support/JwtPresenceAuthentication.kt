package com.nextmall.apigateway.security.support

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtPresenceAuthentication(
    private val token: String,
) : AbstractAuthenticationToken(emptyList()) {
    init {
        isAuthenticated = true
    }

    override fun getName(): String = "gateway-jwt-presence"

    override fun getCredentials(): Any = token

    override fun getPrincipal(): Any =
        mapOf(
            "type" to "jwt-presence",
            "tokenLength" to token.length,
        )
}
