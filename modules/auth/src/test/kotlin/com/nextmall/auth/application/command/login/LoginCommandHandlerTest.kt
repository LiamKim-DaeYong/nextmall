package com.nextmall.auth.application.command.login

import com.nextmall.auth.application.command.login.strategy.AuthLoginStrategy
import com.nextmall.auth.application.query.account.AuthUserAccountContext
import com.nextmall.auth.domain.exception.common.UnsupportedProviderException
import com.nextmall.auth.domain.model.AuthProvider
import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class LoginCommandHandlerTest :
    FunSpec({

        val strategy = mockk<AuthLoginStrategy>()
        val tokenProvider = mockk<TokenProvider>()
        val refreshTokenStore = mockk<RefreshTokenStore>()

        lateinit var handler: LoginCommandHandler

        beforeTest {
            clearMocks(strategy, tokenProvider, refreshTokenStore)

            handler =
                LoginCommandHandler(
                    strategies = listOf(strategy),
                    tokenProvider = tokenProvider,
                    refreshTokenStore = refreshTokenStore,
                )
        }

        test("지원하는 전략이 있으면 토큰을 발급하고 refreshToken을 저장한다") {
            // given
            val param =
                LoginCommandParam(
                    provider = AuthProvider.LOCAL,
                    principal = "test@test.com",
                    credential = "pw",
                )

            val account =
                AuthUserAccountContext(
                    id = 1L,
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "test@test.com",
                    passwordHash = "encoded",
                )

            every { strategy.supports(AuthProvider.LOCAL) } returns true
            every { strategy.login(param) } returns account

            every { tokenProvider.generateAccessToken(1L) } returns "access"
            every { tokenProvider.generateRefreshToken(1L) } returns "refresh"
            every { tokenProvider.refreshTokenTtlSeconds() } returns 3600
            every {
                refreshTokenStore.save(1L, "refresh", 3600)
            } just Runs

            // when
            val result = handler.login(param)

            // then
            result.accessToken shouldBe "access"
            result.refreshToken shouldBe "refresh"

            verify(exactly = 1) {
                refreshTokenStore.save(1L, "refresh", 3600)
            }
        }

        test("지원하는 전략이 없으면 UnsupportedProviderException 발생") {
            val param =
                LoginCommandParam(
                    provider = AuthProvider.GOOGLE,
                    principal = "google-id",
                    credential = null,
                )

            every { strategy.supports(any()) } returns false

            shouldThrow<UnsupportedProviderException> {
                handler.login(param)
            }
        }
    })
