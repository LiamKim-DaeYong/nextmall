package com.nextmall.user.application.usecase

import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.model.UserRole
import com.nextmall.user.domain.repository.UserRepository
import com.nextmall.user.presentation.dto.UserResponse
import com.nextmall.user.presentation.mapper.UserMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class FindUserUseCaseTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val mapper = mockk<UserMapper>()
        val useCase = FindUserUseCase(userRepository, mapper)

        test("정상적으로 사용자 정보를 조회한다") {
            // given
            val user =
                User(
                    email = "test@a.com",
                    password = "pw",
                    nickname = "tester",
                ).apply { id = 1L }

            val response =
                UserResponse(
                    id = 1L,
                    email = "test@a.com",
                    nickname = "tester",
                    role = UserRole.BUYER.name,
                )

            every { userRepository.findById(1L) } returns Optional.of(user)
            every { mapper.toResponse(user) } returns response

            // when
            val result = useCase.findById(1L)

            // then
            result.id shouldBe 1L
            result.email shouldBe "test@a.com"
            result.nickname shouldBe "tester"
        }

        test("존재하지 않는 사용자 조회 시 예외 발생") {
            // given
            every { userRepository.findById(999L) } returns Optional.empty()

            // when & then
            shouldThrow<NoSuchElementException> {
                useCase.findById(999L)
            }
        }
    })
