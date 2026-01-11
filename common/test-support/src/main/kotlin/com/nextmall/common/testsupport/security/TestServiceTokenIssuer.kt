package com.nextmall.common.testsupport.security

import com.nextmall.common.security.internal.ServiceTokenIssuer
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

/**
 * 테스트에서 내부 서비스 토큰을 생성하기 위한 유틸리티.
 *
 * ServiceTokenProperties를 통해 application-test.yml의 설정을 자동으로 사용합니다.
 *
 * 사용법:
 * ```
 * @IntegrationTest
 * class MyTest(
 *     private val testServiceTokenIssuer: TestServiceTokenIssuer
 * ) : FunSpec({
 *
 *     test("서비스 간 통신 테스트") {
 *         val token = testServiceTokenIssuer.issueBearerToken()
 *         webTestClient.post()
 *             .header(ServiceTokenConstants.TOKEN_HEADER, token)
 *             ...
 *     }
 *
 *     test("사용자 대리 요청 테스트") {
 *         val token = testServiceTokenIssuer.issueBearerToken(
 *             userId = "user-123",
 *             roles = setOf("USER"),
 *         )
 *         webTestClient.get()
 *             .header(ServiceTokenConstants.TOKEN_HEADER, token)
 *             ...
 *     }
 * })
 * ```
 */
class TestServiceTokenIssuer(
    private val serviceTokenProperties: ServiceTokenProperties,
) {
    private val secretKey: String
        get() = serviceTokenProperties.secretKey

    /**
     * 테스트용 서비스 토큰을 발행합니다 (사용자 정보 없음).
     */
    fun issueToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String = buildToken(sourceService, targetService, expirationMinutes, null, null)

    /**
     * 사용자 컨텍스트를 포함한 테스트용 서비스 토큰을 발행합니다.
     */
    fun issueToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
        userId: String,
        roles: Set<String>,
    ): String = buildToken(sourceService, targetService, expirationMinutes, userId, roles)

    /**
     * Bearer 접두사가 포함된 토큰을 반환합니다 (사용자 정보 없음).
     */
    fun issueBearerToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String = "Bearer ${issueToken(sourceService, targetService, expirationMinutes)}"

    /**
     * 사용자 컨텍스트를 포함한 Bearer 토큰을 반환합니다.
     */
    fun issueBearerToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
        userId: String,
        roles: Set<String>,
    ): String = "Bearer ${issueToken(sourceService, targetService, expirationMinutes, userId, roles)}"

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
            claimsBuilder.claim(ServiceTokenIssuer.USER_ID_CLAIM, userId)
        }
        if (!roles.isNullOrEmpty()) {
            claimsBuilder.claim(ServiceTokenIssuer.ROLES_CLAIM, roles.toList())
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
