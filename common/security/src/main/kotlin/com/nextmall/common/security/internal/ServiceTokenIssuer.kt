package com.nextmall.common.security.internal

import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nextmall.common.security.token.ServiceTokenProperties
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.Instant
import java.util.Date

abstract class ServiceTokenIssuer(
    private val serviceTokenProperties: ServiceTokenProperties,
) {
    protected abstract val serviceName: String

    fun issueServiceToken(targetService: String): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .subject(serviceName)
            .audience()
            .add(targetService)
            .and()
            .claim(SCOPE_CLAIM, SERVICE_ADMIN_SCOPE)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofMinutes(EXPIRATION_MINUTES))))
            .signWith(SecretKeyDecoder.decode(serviceTokenProperties.secretKey))
            .compact()
    }

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val SERVICE_ADMIN_SCOPE = "service:admin"
        private const val EXPIRATION_MINUTES = 5L
    }
}
