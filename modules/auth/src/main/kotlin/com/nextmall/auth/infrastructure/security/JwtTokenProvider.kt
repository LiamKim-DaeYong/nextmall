package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.UserTokenIssuerProperties
import com.nextmall.auth.domain.token.TokenClaims
import com.nextmall.common.security.jwt.SecretKeyDecoder
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
    private val userTokenProperties: UserTokenIssuerProperties,
) {
    private val key = SecretKeyDecoder.decode(userTokenProperties.secretKey)

    fun generateAccessToken(
        authAccountId: Long,
        roles: List<String>,
    ): String {
        val now = Date()
        val expiry = Date(now.time + userTokenProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(authAccountId.toString())
            .claim("userId", authAccountId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("roles", roles)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(authAccountId: Long): String {
        val now = Date()
        val expiry = Date(now.time + userTokenProperties.refreshTokenExpiration)

        return Jwts
            .builder()
            .subject(authAccountId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun parseAccessToken(token: String): TokenClaims? {
        val claims = getClaims(token) ?: return null

        return TokenClaims(
            authAccountId = claims.subject.toLong(),
            roles = (claims["roles"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            expirationTime = claims.expiration.toInstant(),
        )
    }

    private fun getClaims(token: String): Claims? {
        val cleanToken = token.removePrefix(BEARER_PREFIX).trim()

        return runCatching {
            val jws =
                Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(cleanToken)

            jws.payload
        }.getOrNull()
    }

    fun refreshTokenTtlSeconds(): Long =
        userTokenProperties.refreshTokenExpiration / 1000

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
