package com.nextmall.common.security.internal

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

abstract class PassportTokenIssuer(
    private val passportTokenProperties: PassportTokenProperties,
) {
    protected abstract val serviceName: String

    fun issuePassportToken(
        targetService: String,
        userId: String,
        roles: Set<String>,
    ): String {
        val now = Instant.now()
        val expiration = now.plus(Duration.ofMinutes(EXPIRATION_MINUTES))

        val claimsBuilder =
            JWTClaimsSet
                .Builder()
                .subject(serviceName)
                .audience(targetService)
                .claim(SCOPE_CLAIM, SERVICE_ADMIN_SCOPE)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .claim(USER_ID_CLAIM, userId)
                .claim(ROLES_CLAIM, roles.toList())

        val signedJwt =
            SignedJWT(
                JWSHeader(JWSAlgorithm.HS256),
                claimsBuilder.build(),
            )

        val signer = MACSigner(SecretKeyDecoder.decode(passportTokenProperties.secretKey))
        signedJwt.sign(signer)

        return signedJwt.serialize()
    }

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val SERVICE_ADMIN_SCOPE = "service:admin"
        private const val EXPIRATION_MINUTES = 5L

        const val USER_ID_CLAIM = "user_id"
        const val ROLES_CLAIM = "roles"
    }
}
