package com.nextmall.common.testsupport.security

import com.nextmall.common.security.internal.PassportTokenIssuer
import com.nextmall.common.security.internal.SecurityTokenConstants
import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nextmall.common.security.token.PassportTokenProperties
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Duration
import java.time.Instant
import java.util.Date

class TestPassportTokenIssuer(
    private val passportTokenProperties: PassportTokenProperties,
) {
    private val secretKey: String
        get() = passportTokenProperties.secretKey

    fun issueToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String = buildToken(sourceService, targetService, expirationMinutes, null, null)

    fun issueToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
        userId: String,
        roles: Set<String>,
    ): String = buildToken(sourceService, targetService, expirationMinutes, userId, roles)

    fun issueBearerToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String =
        SecurityTokenConstants.BEARER_PREFIX +
            issueToken(sourceService, targetService, expirationMinutes)

    fun issueBearerToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
        userId: String,
        roles: Set<String>,
    ): String =
        SecurityTokenConstants.BEARER_PREFIX +
            issueToken(sourceService, targetService, expirationMinutes, userId, roles)

    private fun buildToken(
        sourceService: String,
        targetService: String,
        expirationMinutes: Long,
        userId: String?,
        roles: Set<String>?,
    ): String {
        val now = Instant.now()
        val expiration = now.plus(Duration.ofMinutes(expirationMinutes))

        val claimsBuilder =
            JWTClaimsSet
                .Builder()
                .subject(sourceService)
                .audience(targetService)
                .claim(SCOPE_CLAIM, SERVICE_ADMIN_SCOPE)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))

        if (userId != null) {
            claimsBuilder.claim(PassportTokenIssuer.USER_ID_CLAIM, userId)
        }
        if (!roles.isNullOrEmpty()) {
            claimsBuilder.claim(PassportTokenIssuer.ROLES_CLAIM, roles.toList())
        }

        val signedJwt =
            SignedJWT(
                JWSHeader(JWSAlgorithm.HS256),
                claimsBuilder.build(),
            )

        val signer = MACSigner(SecretKeyDecoder.decode(secretKey))
        signedJwt.sign(signer)

        return signedJwt.serialize()
    }

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val SERVICE_ADMIN_SCOPE = "service:admin"
        private const val DEFAULT_EXPIRATION_MINUTES = 5L
        private const val DEFAULT_SOURCE_SERVICE = "test-service"
        private const val DEFAULT_TARGET_SERVICE = "auth-service"
    }
}
