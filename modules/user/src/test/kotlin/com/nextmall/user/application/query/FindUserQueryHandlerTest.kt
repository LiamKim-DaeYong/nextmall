package com.nextmall.user.application.query

import com.nextmall.user.domain.exception.UserNotFoundException
import com.nextmall.user.port.output.UserQueryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class FindUserQueryHandlerTest :
    FunSpec({

        val userQueryPort = mockk<UserQueryPort>()
        lateinit var handler: FindUserQueryHandler

        beforeTest {
            clearMocks(userQueryPort)
            handler = FindUserQueryHandler(userQueryPort)
        }

        // -------------------------------------------------------------------------
        // findById 테스트
        // -------------------------------------------------------------------------
        test("ID 기반 조회 - 정상적으로 사용자 정보를 반환한다") {
            // given
            val userContext =
                UserContext(
                    id = 1L,
                    email = "test@a.com",
                    nickname = "tester",
                )

            every { userQueryPort.findById(1L) } returns userContext

            // when
            val result = handler.findById(1L)

            // then
            result.id shouldBe 1L
            result.email shouldBe "test@a.com"
            result.nickname shouldBe "tester"
        }

        test("ID 기반 조회 - 존재하지 않는 사용자면 예외를 던진다") {
            // given
            every { userQueryPort.findById(999L) } returns null

            // when & then
            shouldThrow<UserNotFoundException> {
                handler.findById(999L)
            }
        }
    })
