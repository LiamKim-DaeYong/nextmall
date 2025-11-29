package com.nextmall.user.application.usecase

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterUserUseCaseTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val idGenerator = mockk<IdGenerator>()

        lateinit var useCase: RegisterUserUseCase

        beforeTest {
            useCase = RegisterUserUseCase(userRepository, passwordEncoder, idGenerator)
        }

        test("정상 회원가입 시 ID 생성, 비밀번호 암호화, 저장이 정상적으로 이루어진다") {
            // given
            every { userRepository.existsByEmail("a@b.com") } returns false
            every { passwordEncoder.encode("pw") } returns "encoded_pw"
            every { idGenerator.generate() } returns 100L

            val savedUserSlot = slot<User>()
            every { userRepository.save(capture(savedUserSlot)) } answers { savedUserSlot.captured }

            // when
            val response = useCase.register("a@b.com", "pw", "nick")

            // then
            savedUserSlot.captured.apply {
                email shouldBe "a@b.com"
                password shouldBe "encoded_pw"
                nickname shouldBe "nick"
                id shouldBe 100L
            }

            response.userId shouldBe 100L
            response.email shouldBe "a@b.com"
        }

        test("중복 이메일이면 DuplicateEmailException 발생") {
            every { userRepository.existsByEmail("a@b.com") } returns true

            shouldThrow<DuplicateEmailException> {
                useCase.register("a@b.com", "pw", "nick")
            }
        }
    })
