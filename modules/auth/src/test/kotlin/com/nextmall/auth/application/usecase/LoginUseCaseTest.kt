package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder

class LoginUseCaseTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val tokenProvider = mockk<TokenProvider>()
        val loginUseCase = LoginUseCase(userRepository, passwordEncoder, tokenProvider)

        test("정상 로그인 시 토큰 두 개가 발급된다") {
            // given
            val user = User(email = "test@a.com", password = "encoded", nickname = "tester")
            every { userRepository.findByEmail("test@a.com") } returns user
            every { passwordEncoder.matches("plain", "encoded") } returns true
            every { tokenProvider.generateAccessToken(any()) } returns "access"
            every { tokenProvider.generateRefreshToken(any()) } returns "refresh"

            // when
            val result = loginUseCase.login("test@a.com", "plain")

            // then
            result.accessToken shouldBe "access"
            result.refreshToken shouldBe "refresh"
        }

        test("비밀번호가 일치하지 않으면 예외 발생") {
            val user = User(email = "test@a.com", password = "encoded", nickname = "tester")
            every { userRepository.findByEmail("test@a.com") } returns user
            every { passwordEncoder.matches(any(), any()) } returns false

            shouldThrow<IllegalArgumentException> {
                loginUseCase.login("test@a.com", "wrong")
            }
        }

        test("존재하지 않는 이메일이면 예외가 발생한다") {
            every { userRepository.findByEmail("none@test.com") } returns null

            shouldThrow<IllegalArgumentException> {
                loginUseCase.login("none@test.com", "password")
            }
        }
    })
