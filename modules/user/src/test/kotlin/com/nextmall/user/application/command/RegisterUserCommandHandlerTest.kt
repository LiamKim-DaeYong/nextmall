package com.nextmall.user.application.command

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.user.application.port.output.PasswordHasher
import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.entity.User
import com.nextmall.user.application.port.output.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.LocalDateTime
import java.time.ZoneOffset

class RegisterUserCommandHandlerTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val passwordHasher = mockk<PasswordHasher>()
        val idGenerator = mockk<IdGenerator>()

        lateinit var handler: RegisterUserCommandHandler

        beforeTest {
            clearMocks(userRepository, passwordHasher, idGenerator)
            handler = RegisterUserCommandHandler(userRepository, passwordHasher, idGenerator)
        }

        test("정상 회원가입 시 ID 생성, 비밀번호 암호화, 저장이 정상적으로 이루어진다") {
            // given
            every { userRepository.existsByEmail("a@b.com") } returns false
            every { passwordHasher.encode("pw") } returns "encoded_pw"
            every { idGenerator.generate() } returns 100L

            val savedUserSlot = slot<User>()
            every { userRepository.save(capture(savedUserSlot)) } answers {
                savedUserSlot.captured.apply {
                    createdAt = LocalDateTime.now(ZoneOffset.UTC)
                    updatedAt = LocalDateTime.now(ZoneOffset.UTC)
                }
            }

            // when
            val response = handler.register("a@b.com", "pw", "nick")

            // then
            savedUserSlot.captured.apply {
                email shouldBe "a@b.com"
                password shouldBe "encoded_pw"
                nickname shouldBe "nick"
                id shouldBe 100L
            }

            response.id shouldBe 100L
            response.email shouldBe "a@b.com"
        }

        test("중복 이메일이면 DuplicateEmailException 발생") {
            every { userRepository.existsByEmail("a@b.com") } returns true

            shouldThrow<DuplicateEmailException> {
                handler.register("a@b.com", "pw", "nick")
            }
        }
    })
