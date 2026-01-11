package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.UserTokenIssuerProperties
import com.nextmall.auth.domain.token.TokenClaims
import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
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

        // subject: 인증 주체 (어떤 auth_account로 로그인했는지)
        // userId: 비즈니스 사용자 ID (누구인지)
        // 현재는 1:1이지만, 멀티 프로바이더 연동 시 다른 값이 될 수 있음
        // 예: 구글 계정(authAccountId=2)으로 로그인해도 userId=1인 경우
        val claims =
            JWTClaimsSet
                .Builder()
                .subject(authAccountId.toString())
                .claim("userId", authAccountId.toString())
                .issueTime(now)
                .expirationTime(expiry)
                .claim("roles", roles)
                .build()

        val signedJwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
        signedJwt.sign(MACSigner(key))

        return signedJwt.serialize()
    }

    fun generateRefreshToken(authAccountId: Long): String {
        val now = Date()
        val expiry = Date(now.time + userTokenProperties.refreshTokenExpiration)

        val claims =
            JWTClaimsSet
                .Builder()
                .subject(authAccountId.toString())
                .issueTime(now)
                .expirationTime(expiry)
                .build()

        val signedJwt = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claims)
        signedJwt.sign(MACSigner(key))

        return signedJwt.serialize()
    }

    fun parseAccessToken(token: String): TokenClaims? {
        val claims = getClaims(token) ?: return null

        @Suppress("UNCHECKED_CAST")
        val roles = (claims.getClaim("roles") as? List<String>) ?: emptyList()

        return TokenClaims(
            authAccountId = claims.subject.toLong(),
            roles = roles,
            expirationTime = claims.expirationTime.toInstant(),
        )
    }

    private fun getClaims(token: String): JWTClaimsSet? {
        val cleanToken = token.removePrefix(BEARER_PREFIX).trim()

        return runCatching {
            val signedJwt = SignedJWT.parse(cleanToken)
            val verifier = MACVerifier(key)

            if (!signedJwt.verify(verifier)) {
                return null
            }

            val claims = signedJwt.jwtClaimsSet
            val now = Date()

            if (claims.expirationTime != null && claims.expirationTime.before(now)) {
                return null
            }

            claims
        }.getOrNull()
    }

    fun refreshTokenTtlSeconds(): Long =
        userTokenProperties.refreshTokenExpiration / 1000

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
