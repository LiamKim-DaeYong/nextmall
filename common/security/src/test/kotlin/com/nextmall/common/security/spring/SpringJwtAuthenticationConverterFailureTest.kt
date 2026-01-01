package com.nextmall.common.security.spring

import com.nextmall.common.security.principal.JwtToPrincipalConverter
import com.nextmall.common.testsupport.jwt.TestJwtFactory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class SpringJwtAuthenticationConverterFailureTest :
    FunSpec({

        test("userId claim이 없으면 예외가 발생한다") {
            // given
            val jwt = TestJwtFactory.missingUserId()

            val principalConverter = JwtToPrincipalConverter()
            val converter = SpringJwtAuthenticationConverter(principalConverter)

            // expect
            shouldThrow<IllegalArgumentException> {
                converter.convert(jwt)
            }
        }
    })
