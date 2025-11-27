package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidLoginException
import com.nextmall.auth.domain.exception.TooManyLoginAttemptsException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.infrastructure.redis.RateLimitRepository
import com.nextmall.user.domain.model.User
import com.nextmall.user.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class LoginUseCaseTest :
    FunSpec({

        val userRepository = mockk<UserRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val tokenProvider = mockk<TokenProvider>()
        val rateLimitRepository = mockk<RateLimitRepository>()

        lateinit var loginUseCase: LoginUseCase

        beforeTest {
            loginUseCase =
                LoginUseCase(
                    userRepository = userRepository,
                    passwordEncoder = passwordEncoder,
                    tokenProvider = tokenProvider,
                    rateLimitRepository = rateLimitRepository,
                )
        }

        test("정상 로그인 시 토큰 두 개가 발급된다") {
            // given
            val user = User(id = 1L, email = "test@a.com", password = "encoded", nickname = "tester")
            every { userRepository.findByEmail("test@a.com") } returns user
            every { passwordEncoder.matches("plain", "encoded") } returns true
            every { tokenProvider.generateAccessToken(any()) } returns "access"
            every { tokenProvider.generateRefreshToken(any()) } returns "refresh"
            every { rateLimitRepository.resetFailCount(any()) } just Runs
            every { rateLimitRepository.getFailCount(any()) } returns 0

            // when
            val result = loginUseCase.login("test@a.com", "plain")

            // then
            result.accessToken shouldBe "access"
            result.refreshToken shouldBe "refresh"

            verify(exactly = 1) { rateLimitRepository.resetFailCount("test@a.com") }
        }

        test("비밀번호가 일치하지 않으면 예외 발생") {
            val user = User(id = 2L, email = "test2@a.com", password = "encoded", nickname = "tester")
            every { userRepository.findByEmail("test2@a.com") } returns user
            every { passwordEncoder.matches(any(), any()) } returns false
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 0

            shouldThrow<InvalidLoginException> {
                loginUseCase.login("test2@a.com", "wrong")
            }
        }

        test("존재하지 않는 이메일이면 예외가 발생한다") {
            every { userRepository.findByEmail("none@test.com") } returns null
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 0

            shouldThrow<InvalidLoginException> {
                loginUseCase.login("none@test.com", "password")
            }
        }

        test("로그인 실패 횟수가 5 이상이면 TooManyLoginAttemptsException 발생") {
            every { rateLimitRepository.getFailCount("test4@a.com") } returns 5

            shouldThrow<TooManyLoginAttemptsException> {
                loginUseCase.login("test4@a.com", "pw")
            }
        }

        test("비밀번호가 틀리면 실패 횟수가 증가한다") {
            val user = User(id = 5L, email = "test5@a.com", password = "encoded", nickname = "nick")
            every { userRepository.findByEmail("test5@a.com") } returns user
            every { passwordEncoder.matches(any(), any()) } returns false
            every { rateLimitRepository.getFailCount("test5@a.com") } returns 0
            every { rateLimitRepository.increaseFailCount("test5@a.com") } returns 1

            shouldThrow<InvalidLoginException> {
                loginUseCase.login("test5@a.com", "pw")
            }

            verify(exactly = 1) { rateLimitRepository.increaseFailCount("test5@a.com") }
        }
    })
