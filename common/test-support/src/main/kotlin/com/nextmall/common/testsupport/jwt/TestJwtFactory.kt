package com.nextmall.common.testsupport.jwt

import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

object TestJwtFactory {
    fun valid(
        subject: String = "test-subject",
        userId: String = "user-123",
        expiresInSeconds: Long = 60,
    ): Jwt =
        Jwt(
            "test-token",
            Instant.now(),
            Instant.now().plusSeconds(expiresInSeconds),
            mapOf("alg" to "none"),
            mapOf(
                "sub" to subject,
                "userId" to userId,
            ),
        )

    fun missingUserId(
        subject: String = "test-subject",
    ): Jwt =
        Jwt(
            "test-token",
            Instant.now(),
            Instant.now().plusSeconds(60),
            mapOf("alg" to "none"),
            mapOf(
                "sub" to subject,
                // userId 없음
            ),
        )
}
