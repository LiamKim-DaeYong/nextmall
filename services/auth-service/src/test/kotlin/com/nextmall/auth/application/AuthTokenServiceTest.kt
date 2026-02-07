package com.nextmall.auth.application

import com.nextmall.auth.application.exception.UnsupportedProviderException
import com.nextmall.auth.application.login.LoginStrategy
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
import com.nextmall.auth.infrastructure.cache.RefreshTokenStore
import com.nextmall.auth.infrastructure.security.JwtTokenProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthTokenServiceTest :
    FunSpec({
        val loginStrategy = mockk<LoginStrategy>()
        val tokenProvider = mockk<JwtTokenProvider>()
        val refreshTokenStore = mockk<RefreshTokenStore>()
        val service = AuthTokenService(listOf(loginStrategy), tokenProvider, refreshTokenStore)

        beforeTest {
            clearMocks(loginStrategy, tokenProvider, refreshTokenStore)
        }

        test("지원하지 않는 provider면 예외가 발생한다") {
            every { loginStrategy.supports(AuthProvider.LOCAL) } returns false

            shouldThrow<UnsupportedProviderException> {
                service.login(AuthProvider.LOCAL, "user", "pw")
            }
        }

        test("로그인 성공 시 토큰을 발급하고 refreshToken을 저장한다") {
            every { loginStrategy.supports(AuthProvider.LOCAL) } returns true
            every { loginStrategy.authenticate("user", "pw") } returns 10L
            every { tokenProvider.generateAccessToken(10L, listOf("ROLE_USER")) } returns "access"
            every { tokenProvider.generateRefreshToken(10L) } returns "refresh"
            every { tokenProvider.refreshTokenTtlSeconds() } returns 3600L
            every { refreshTokenStore.save("refresh", 10L, 3600L) } returns Unit

            val result = service.login(AuthProvider.LOCAL, "user", "pw")

            result.accessToken shouldBe "access"
            result.refreshToken shouldBe "refresh"
            verify(exactly = 1) { refreshTokenStore.save("refresh", 10L, 3600L) }
        }

        test("refresh는 기존 토큰을 폐기하고 새 토큰을 발급한다") {
            every { refreshTokenStore.findAuthAccountId("old") } returns 20L
            every { refreshTokenStore.delete("old") } returns true
            every { tokenProvider.generateAccessToken(20L, listOf("ROLE_USER")) } returns "new-access"
            every { tokenProvider.generateRefreshToken(20L) } returns "new-refresh"
            every { tokenProvider.refreshTokenTtlSeconds() } returns 1800L
            every { refreshTokenStore.save("new-refresh", 20L, 1800L) } returns Unit

            val result = service.refresh("old")

            result.accessToken shouldBe "new-access"
            result.refreshToken shouldBe "new-refresh"
            verify(exactly = 1) { refreshTokenStore.delete("old") }
        }

        test("refresh 토큰이 없으면 예외가 발생한다") {
            every { refreshTokenStore.findAuthAccountId("missing") } returns null

            shouldThrow<InvalidRefreshTokenException> {
                service.refresh("missing")
            }
        }

        test("revoke는 삭제 실패를 무시한다") {
            every { refreshTokenStore.delete("token") } throws RuntimeException("redis error")

            service.revoke("token")

            verify(exactly = 1) { refreshTokenStore.delete("token") }
        }
    })
