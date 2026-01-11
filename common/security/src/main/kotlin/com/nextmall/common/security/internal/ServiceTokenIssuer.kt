package com.nextmall.common.security.internal

import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nextmall.common.security.token.ServiceTokenProperties
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Duration
import java.time.Instant
import java.util.Date

abstract class ServiceTokenIssuer(
    private val serviceTokenProperties: ServiceTokenProperties,
) {
    protected abstract val serviceName: String

    /**
     * 서비스 간 인증용 토큰 발급 (사용자 정보 없음).
     * 시스템 간 통신에 사용.
     */
    fun issueServiceToken(targetService: String): String = buildToken(targetService, null, null)

    /**
     * 사용자 컨텍스트를 포함한 서비스 토큰 발급.
     * 사용자 요청을 대신 처리하는 내부 서비스 호출에 사용.
     */
    fun issueServiceToken(
        targetService: String,
        userId: String,
        roles: Set<String>,
    ): String = buildToken(targetService, userId, roles)

    private fun buildToken(
        targetService: String,
        userId: String?,
        roles: Set<String>?,
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

        if (userId != null) {
            claimsBuilder.claim(USER_ID_CLAIM, userId)
        }
        if (!roles.isNullOrEmpty()) {
            claimsBuilder.claim(ROLES_CLAIM, roles.toList())
        }

        val signedJwt =
            SignedJWT(
                JWSHeader(JWSAlgorithm.HS256),
                claimsBuilder.build(),
            )

        val signer = MACSigner(SecretKeyDecoder.decode(serviceTokenProperties.secretKey))
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
