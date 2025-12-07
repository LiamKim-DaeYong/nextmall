package com.nextmall.user.application.query

import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.infrastructure.jooq.UserQueryRepositoryImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.OffsetDateTime

class FindUserQueryHandlerTest :
    FunSpec({

        val userQueryRepository = mockk<UserQueryRepositoryImpl>()
        lateinit var useCase: FindUserQueryHandler

        beforeTest {
            clearMocks(userQueryRepository)
            useCase = FindUserQueryHandler(userQueryRepository)
        }

        // -------------------------------------------------------------------------
        // findById 테스트
        // -------------------------------------------------------------------------
        test("ID 기반 조회 - 정상적으로 사용자 정보를 반환한다") {
            // given
            val now = OffsetDateTime.parse("2025-01-01T12:00:00Z")

            val userView =
                UserView(
                    id = 1L,
                    email = "test@a.com",
                    nickname = "tester",
                    provider = "LOCAL",
                    role = "BUYER",
                    createdAt = now,
                )

            every { userQueryRepository.findById(1L) } returns userView

            // when
            val result = useCase.findById(1L)

            // then
            result.id shouldBe 1L
            result.email shouldBe "test@a.com"
            result.nickname shouldBe "tester"
            result.createdAt shouldBe now
        }

        test("ID 기반 조회 - 존재하지 않는 사용자면 예외를 던진다") {
            // given
            every { userQueryRepository.findById(999L) } returns null

            // when & then
            shouldThrow<NoSuchElementException> {
                useCase.findById(999L)
            }
        }

        // -------------------------------------------------------------------------
        // findByEmail 테스트
        // -------------------------------------------------------------------------
        test("Email 기반 조회 - 정상적으로 사용자 정보를 반환한다") {
            // given
            val now = OffsetDateTime.parse("2025-01-01T12:00:00Z")

            val userView =
                UserView(
                    id = 10L,
                    email = "hello@a.com",
                    nickname = "emailTester",
                    provider = "LOCAL",
                    role = "BUYER",
                    createdAt = now,
                )

            every { userQueryRepository.findByEmail("hello@a.com") } returns userView

            // when
            val result = useCase.findByEmail("hello@a.com")

            // then
            result.id shouldBe 10L
            result.email shouldBe "hello@a.com"
            result.nickname shouldBe "emailTester"
            result.createdAt shouldBe now
        }

        test("Email 기반 조회 - 존재하지 않는 사용자면 예외를 던진다") {
            // given
            every { userQueryRepository.findByEmail("nope@a.com") } returns null

            // when & then
            shouldThrow<NoSuchElementException> {
                useCase.findByEmail("nope@a.com")
            }
        }
    })
