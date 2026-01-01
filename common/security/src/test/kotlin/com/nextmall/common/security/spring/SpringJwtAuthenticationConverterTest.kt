package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.principal.JwtToPrincipalConverter
import com.nextmall.common.testsupport.jwt.TestJwtFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class SpringJwtAuthenticationConverterTest :
    FunSpec({

        test("JWT가 주어지면 AuthenticatedPrincipal을 생성한다") {
            // given
            val jwt =
                TestJwtFactory.valid(
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
