package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidLoginException
import com.nextmall.auth.domain.exception.TooManyLoginAttemptsException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.model.LoginIdentity
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import com.nextmall.auth.infrastructure.redis.RateLimitRepository
import com.nextmall.user.application.query.dto.UserCredentials
import com.nextmall.user.domain.model.AuthProvider
import com.nextmall.user.domain.repository.UserCredentialsQueryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class LoginUseCaseTest :
    FunSpec({

        val credentialsRepository = mockk<UserCredentialsQueryRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val tokenProvider = mockk<TokenProvider>()
        val rateLimitRepository = mockk<RateLimitRepository>()
        val refreshTokenStore = mockk<RefreshTokenStore>()

        lateinit var userCase: LoginUseCase

        beforeTest {
            clearMocks(credentialsRepository, passwordEncoder, tokenProvider, rateLimitRepository, refreshTokenStore)
            userCase =
                LoginUseCase(
                    credentialsRepository = credentialsRepository,
                    passwordEncoder = passwordEncoder,
                    tokenProvider = tokenProvider,
                    rateLimitRepository = rateLimitRepository,
                    refreshTokenStore = refreshTokenStore,
                )
        }

        test("정상 로그인 시 토큰 두 개가 발급되고 RefreshToken이 저장된다") {
            // given
            val userCredentials = UserCredentials(id = 1L, password = "encoded", role = "BUYER")

            every { credentialsRepository.findByEmail("test@a.com") } returns userCredentials
            every { passwordEncoder.matches("plain", "encoded") } returns true
            every { tokenProvider.generateAccessToken("1", any()) } returns "access"
            every { tokenProvider.generateRefreshToken("1") } returns "refresh"
            every { tokenProvider.refreshTokenTtlSeconds() } returns 3600
            every { refreshTokenStore.save(1L, "refresh", 3600) } just Runs
            every { rateLimitRepository.resetFailCount(any()) } just Runs
            every { rateLimitRepository.getFailCount(any()) } returns 0

            // when
            val result = userCase.login("test@a.com", "plain")

            // then
            result.accessToken shouldBe "access"
            result.refreshToken shouldBe "refresh"

            verify(exactly = 1) {
                rateLimitRepository.resetFailCount(any())
                refreshTokenStore.save(1L, "refresh", 3600)
            }
        }

        test("비밀번호가 일치하지 않으면 예외 발생") {
            val userCredentials = UserCredentials(id = 2L, password = "encoded", role = "BUYER")

            every { credentialsRepository.findByEmail("test2@a.com") } returns userCredentials
            every { passwordEncoder.matches(any(), any()) } returns false
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 0

            shouldThrow<InvalidLoginException> {
                userCase.login("test2@a.com", "wrong")
            }
        }

        test("존재하지 않는 이메일이면 예외가 발생한다") {
            every { credentialsRepository.findByEmail("none@test.com") } returns null
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 0

            shouldThrow<InvalidLoginException> {
                userCase.login("none@test.com", "password")
            }
        }

        test("로그인 실패 횟수가 5 이상이면 TooManyLoginAttemptsException 발생") {
            every { rateLimitRepository.getFailCount(any()) } returns 5

            shouldThrow<TooManyLoginAttemptsException> {
                userCase.login("test4@a.com", "pw")
            }
        }

        test("비밀번호가 틀리면 실패 횟수가 증가한다") {
            val userCredentials = UserCredentials(id = 3L, password = "encoded", role = "BUYER")

            every { credentialsRepository.findByEmail("test5@a.com") } returns userCredentials
            every { passwordEncoder.matches(any(), any()) } returns false
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 1

            shouldThrow<InvalidLoginException> {
                userCase.login("test5@a.com", "pw")
            }

            verify(exactly = 1) {
                rateLimitRepository.increaseFailCount(
                    match { it.identifier == "test5@a.com" && it.provider == AuthProvider.LOCAL },
                )
            }
        }

        test("비밀번호가 없는 계정이면 실패 카운트가 증가하고 InvalidLoginException 발생") {
            val userCredentials = UserCredentials(id = 4L, password = null, role = "BUYER")

            every { credentialsRepository.findByEmail("test6@a.com") } returns userCredentials
            every { rateLimitRepository.getFailCount(any()) } returns 0
            every { rateLimitRepository.increaseFailCount(any()) } returns 1

            shouldThrow<InvalidLoginException> {
                userCase.login("test6@a.com", "pw")
            }

            verify(exactly = 1) {
                rateLimitRepository.increaseFailCount(
                    LoginIdentity.local("test6@a.com"),
                )
            }
        }
    })
