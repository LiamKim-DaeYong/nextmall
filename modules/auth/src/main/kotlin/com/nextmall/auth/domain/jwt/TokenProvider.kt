package com.nextmall.auth.domain.jwt

import com.nextmall.auth.config.JwtProperties
import com.nextmall.auth.domain.exception.InvalidJwtConfigException
import com.nextmall.user.domain.model.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date

@Component
class TokenProvider(
    private val jwtProperties: JwtProperties,
) {
    private val key =
        run {
            val decoded =
                runCatching { Base64.getDecoder().decode(jwtProperties.secretKey) }
                    .getOrElse { jwtProperties.secretKey.toByteArray() }

            if (decoded.size < MIN_SECRET_KEY_SIZE) {
                throw InvalidJwtConfigException(
                    "JWT secret key must be at least 256 bits (32 bytes). " +
                        "Current: ${decoded.size} bytes",
                )
            }

            Keys.hmacShaKeyFor(decoded)
        }

    fun generateAccessToken(subject: String, role: UserRole): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(subject)
            .claim("roles", listOf(role.name))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(subject: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun getClaims(token: String): Claims {
        val cleanToken = token.removePrefix(jwtProperties.tokenPrefix)

        val jws =
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(cleanToken)

        return jws.payload
    }

    // --- Refresh Token Helper Functions ---
    fun parseRefreshToken(token: String): Long {
        val claims = getClaims(token)
        return claims.subject.toLongOrNull()
            ?: throw InvalidJwtConfigException("Invalid refresh token subject: ${claims.subject}")
    }

    fun refreshTokenTtlSeconds(): Long =
        jwtProperties.refreshTokenExpiration / 1000

    companion object {
        private const val MIN_SECRET_KEY_SIZE = 32 // bytes = 256 bits
    }
}
