package com.nextmall.user.application.usecase

import com.nextmall.user.domain.exception.DuplicateEmailException
import com.nextmall.user.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterUserUseCaseTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val useCase = RegisterUserUseCase(userRepository, passwordEncoder)

        test("중복 이메일이면 DuplicateEmailException 발생") {
            every { userRepository.existsByEmail("a@b.com") } returns true

            shouldThrow<DuplicateEmailException> {
                useCase.register("a@b.com", "pw", "nick")
            }
        }
    })
