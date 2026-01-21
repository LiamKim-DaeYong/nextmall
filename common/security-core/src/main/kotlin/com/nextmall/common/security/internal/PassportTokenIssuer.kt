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
        val expiration = now.plus(Duration.ofSeconds(EXPIRATION_SECONDS))

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

        /**
         * Passport Token 만료 시간 (30초)
         *
         * Netflix Passport 패턴에서 Passport는 단일 요청 처리 중에만 유효하면 된다.
         * - 네트워크 지연 + 서비스 처리 시간만 커버 (보통 수 초)
         * - 탈취되어도 30초 후 무효화
         * - 내부 서비스 간 통신용이므로 refresh 불필요
         */
        private const val EXPIRATION_SECONDS = 30L

        const val USER_ID_CLAIM = "user_id"
        const val ROLES_CLAIM = "roles"
    }
}
