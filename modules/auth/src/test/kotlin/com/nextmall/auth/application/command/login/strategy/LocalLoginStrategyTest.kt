package com.nextmall.auth.application.command.login.strategy

import com.nextmall.auth.application.query.account.AuthAccountContext
import com.nextmall.auth.application.exception.InvalidLoginException
import com.nextmall.auth.application.exception.TooManyLoginAttemptsException
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.application.login.LoginIdentity
import com.nextmall.auth.application.login.LocalLoginStrategy
import com.nextmall.auth.infrastructure.cache.RateLimitRepository
import com.nextmall.auth.port.output.account.AuthUserAccountQueryPort
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

class LocalLoginStrategyTest :
    FunSpec({

        val accountQueryPort = mockk<AuthUserAccountQueryPort>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val rateLimitRepository = mockk<RateLimitRepository>()

        lateinit var strategy: LocalLoginStrategy

        beforeTest {
            clearMocks(accountQueryPort, passwordEncoder, rateLimitRepository)

            strategy =
                LocalLoginStrategy(
                    authUserAccountQueryPort = accountQueryPort,
                    passwordEncoder = passwordEncoder,
                    rateLimitRepository = rateLimitRepository,
                )
        }

        test("비밀번호가 일치하면 AuthUserAccountContext를 반환하고 실패 횟수를 초기화한다") {
            val param =
                LoginCommandParam(
                    provider = AuthProvider.LOCAL,
                    principal = "test@test.com",
                    credential = "pw",
                )

            val identity = LoginIdentity.local("test@test.com")

            val account =
                AuthAccountContext(
                    id = 1L,
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "test@test.com",
                    passwordHash = "encoded",
                )

            every { rateLimitRepository.getFailCount(identity) } returns 0
            every {
                accountQueryPort.findByProviderAndAccountId(
                    AuthProvider.LOCAL,
                    "test@test.com",
                )
            } returns account

            every { passwordEncoder.matches("pw", "encoded") } returns true
            every { rateLimitRepository.resetFailCount(identity) } just Runs

            val result = strategy.login(param)

            result.userId shouldBe 1L

            verify {
                rateLimitRepository.resetFailCount(identity)
            }
        }

        test("비밀번호가 틀리면 실패 횟수를 증가시키고 InvalidLoginException 발생") {
            val param =
                LoginCommandParam(
                    provider = AuthProvider.LOCAL,
                    principal = "fail@test.com",
                    credential = "wrong",
                )

            val identity = LoginIdentity.local("fail@test.com")

            val account =
                AuthAccountContext(
                    id = 2L,
                    userId = 2L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "fail@test.com",
                    passwordHash = "encoded",
                )

            every { rateLimitRepository.getFailCount(identity) } returns 0
            every {
                accountQueryPort.findByProviderAndAccountId(
                    AuthProvider.LOCAL,
                    "fail@test.com",
                )
            } returns account

            every { passwordEncoder.matches(any(), any()) } returns false
            every { rateLimitRepository.increaseFailCount(identity) } returns 1

            shouldThrow<InvalidLoginException> {
                strategy.login(param)
            }

            verify {
                rateLimitRepository.increaseFailCount(identity)
            }
        }

        test("실패 횟수가 5 이상이면 TooManyLoginAttemptsException 발생") {
            val param =
                LoginCommandParam(
                    provider = AuthProvider.LOCAL,
                    principal = "blocked@test.com",
                    credential = "pw",
                )

            val identity = LoginIdentity.local("blocked@test.com")

            every { rateLimitRepository.getFailCount(identity) } returns 5

            shouldThrow<TooManyLoginAttemptsException> {
                strategy.login(param)
            }
        }
    })
