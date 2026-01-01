package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.token.TokenClaims
import com.nextmall.auth.infrastructure.security.exception.InvalidJwtConfigException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) {
    private val key =
        run {
            val decoded =
                runCatching { Base64.getDecoder().decode(jwtProperties.secretKey) }
                    .getOrElse { jwtProperties.secretKey.toByteArray() }

            if (decoded.size < MIN_SECRET_KEY_SIZE) {
                throw InvalidJwtConfigException()
            }

            Keys.hmacShaKeyFor(decoded)
        }

    // --- AccessToken 생성 ---
    fun generateAccessToken(
        authAccountId: Long,
        roles: List<String>,
    ): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(authAccountId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("roles", roles)
            .signWith(key)
            .compact()
    }

    // --- RefreshToken 생성 ---
    fun generateRefreshToken(authAccountId: Long): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts
            .builder()
            .subject(authAccountId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    // --- AccessToken Claims 파싱 ---
    fun parseAccessToken(token: String): TokenClaims? {
        val claims = getClaims(token) ?: return null

        return TokenClaims(
            authAccountId = claims.subject.toLong(),
            roles = (claims["roles"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            expirationTime = claims.expiration.toInstant(),
        )
    }

    // --- 내부 Claims 파서 ---
    private fun getClaims(token: String): Claims? {
        val cleanToken = token.removePrefix(jwtProperties.tokenPrefix).trim()

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
        jwtProperties.refreshTokenExpiration / 1000

    companion object {
        private const val MIN_SECRET_KEY_SIZE = 32
    }
}
