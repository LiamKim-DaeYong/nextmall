package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.JwtToPrincipalConverter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class SpringJwtAuthenticationConverterFailureTest :
    FunSpec({

        test("userId claim이 없으면 예외가 발생한다") {
            // given
            val jwt = createJwtWithoutUserId()

            val principalConverter = JwtToPrincipalConverter()
            val converter = SpringJwtAuthenticationConverter(principalConverter)

            // expect
            shouldThrow<IllegalArgumentException> {
                converter.convert(jwt)
            }
        }
    })

private fun createJwtWithoutUserId(): Jwt =
    Jwt(
        "test-token",
        Instant.now(),
        Instant.now().plusSeconds(60),
        mapOf("alg" to "none"),
        mapOf("sub" to "test-subject"),
    )
