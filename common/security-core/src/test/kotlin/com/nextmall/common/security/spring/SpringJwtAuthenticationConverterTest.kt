package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.principal.JwtToPrincipalConverter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.time.Instant

class SpringJwtAuthenticationConverterTest :
    FunSpec({

        test("JWT가 주어지면 AuthenticatedPrincipal을 생성한다") {
            // given
            val jwt =
                createTestJwt(
                    subject = "test-subject",
                    userId = "user-123",
                )

            val principalConverter = JwtToPrincipalConverter()
            val converter = SpringJwtAuthenticationConverter(principalConverter)

            // when
            val authentication = converter.convert(jwt)

            // then
            authentication.shouldBeInstanceOf<JwtAuthenticationToken>()
            authentication.name shouldBe "user-123"

            val principal = authentication.details as AuthenticatedPrincipal
            principal.subject shouldBe "test-subject"
            principal.userId shouldBe "user-123"
        }
    })

private fun createTestJwt(
    subject: String,
    userId: String,
): Jwt =
    Jwt(
        "test-token",
        Instant.now(),
        Instant.now().plusSeconds(60),
        mapOf("alg" to "none"),
        mapOf(
            "sub" to subject,
            "userId" to userId,
        ),
    )
