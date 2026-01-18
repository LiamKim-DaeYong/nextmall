package com.nextmall.auth.infrastructure.security

import com.nextmall.auth.config.ExternalTokenProperties
import com.nextmall.auth.domain.token.TokenClaims
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date

@Component
class JwtTokenProvider(
    private val externalTokenProperties: ExternalTokenProperties,
) {
    private val privateKey: RSAPrivateKey = loadPrivateKey(externalTokenProperties.privateKey)
    private val publicKey: RSAPublicKey = loadPublicKey(externalTokenProperties.publicKey)
    private val keyId: String = externalTokenProperties.keyId

    fun generateAccessToken(
        authAccountId: Long,
        roles: List<String>,
    ): String {
        val now = Date()
        val expiry = Date(now.time + externalTokenProperties.accessTokenExpiration)

        val claims =
            JWTClaimsSet
                .Builder()
                .subject(authAccountId.toString())
                .claim("userId", authAccountId.toString())
                .issueTime(now)
                .expirationTime(expiry)
                .claim("roles", roles)
                .build()

        val signedJwt =
            SignedJWT(
                JWSHeader
                    .Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .build(),
                claims,
            )
        signedJwt.sign(RSASSASigner(privateKey))

        return signedJwt.serialize()
    }

    fun generateRefreshToken(authAccountId: Long): String {
        val now = Date()
        val expiry = Date(now.time + externalTokenProperties.refreshTokenExpiration)

        val claims =
            JWTClaimsSet
                .Builder()
                .subject(authAccountId.toString())
                .issueTime(now)
                .expirationTime(expiry)
                .build()

        val signedJwt =
            SignedJWT(
                JWSHeader
                    .Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .build(),
                claims,
            )
        signedJwt.sign(RSASSASigner(privateKey))

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
            val verifier = RSASSAVerifier(publicKey)

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
        externalTokenProperties.refreshTokenExpiration / 1000

    fun getPublicKey(): RSAPublicKey = publicKey

    companion object {
        private const val BEARER_PREFIX = "Bearer "

        private fun loadPrivateKey(base64Key: String): RSAPrivateKey {
            val keyBytes = Base64.getDecoder().decode(base64Key)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        }

        private fun loadPublicKey(base64Key: String): RSAPublicKey {
            val keyBytes = Base64.getDecoder().decode(base64Key)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(keySpec) as RSAPublicKey
        }
    }
}
