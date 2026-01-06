package com.nextmall.common.testsupport.security

import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nextmall.common.security.token.ServiceTokenProperties
import io.jsonwebtoken.Jwts
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
 *     test("테스트") {
 *         val token = testServiceTokenIssuer.issueBearerToken()
 *         webTestClient.post()
 *             .header("Authorization", token)
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
     * 테스트용 서비스 토큰을 발행합니다.
     *
     * @param sourceService 토큰을 발행하는 서비스 이름 (subject)
     * @param targetService 토큰을 사용할 대상 서비스 이름 (audience)
     * @param expirationMinutes 토큰 만료 시간 (분)
     * @return JWT 토큰 문자열
     */
    fun issueToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(sourceService)
            .audience()
            .add(targetService)
            .and()
            .claim(SCOPE_CLAIM, SERVICE_ADMIN_SCOPE)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
            .signWith(SecretKeyDecoder.decode(secretKey))
            .compact()
    }

    /**
     * Bearer 접두사가 포함된 토큰을 반환합니다.
     */
    fun issueBearerToken(
        sourceService: String = DEFAULT_SOURCE_SERVICE,
        targetService: String = DEFAULT_TARGET_SERVICE,
        expirationMinutes: Long = DEFAULT_EXPIRATION_MINUTES,
    ): String = "Bearer ${issueToken(sourceService, targetService, expirationMinutes)}"

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val SERVICE_ADMIN_SCOPE = "service:admin"
        private const val DEFAULT_EXPIRATION_MINUTES = 5L
        private const val DEFAULT_SOURCE_SERVICE = "test-service"
        private const val DEFAULT_TARGET_SERVICE = "auth-service"
    }
}
