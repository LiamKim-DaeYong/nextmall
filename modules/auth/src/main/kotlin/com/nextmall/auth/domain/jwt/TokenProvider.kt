package com.nextmall.auth.domain.jwt

import com.nextmall.auth.config.JwtProperties
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
                try {
                    Base64.getDecoder().decode(jwtProperties.secretKey)
                } catch (_: IllegalArgumentException) {
                    jwtProperties.secretKey.toByteArray()
                }

            require(decoded.size >= 32) {
                "JWT secret key must be at least 256 bits (32 bytes)"
            }

            Keys.hmacShaKeyFor(decoded)
        }

    fun generateAccessToken(subject: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(subject)
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
}
