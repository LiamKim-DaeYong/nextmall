package com.nextmall.auth.application.command.token

import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
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

class RefreshTokenCommandHandlerTest :
    FunSpec({

        val tokenProvider = mockk<TokenProvider>()
        val refreshTokenStore = mockk<RefreshTokenStore>()

        lateinit var handler: RefreshTokenCommandHandler

        beforeTest {
            clearMocks(tokenProvider, refreshTokenStore)
            handler = RefreshTokenCommandHandler(tokenProvider, refreshTokenStore)
        }

        test("refresh 성공 시 새 accessToken과 refreshToken을 발급하고 저장한다") {
            // given
            val oldRefreshToken = "old-refresh"
            val userId = 1L
            val newAccessToken = "new-access"
            val newRefreshToken = "new-refresh"

            every { tokenProvider.parseRefreshToken(oldRefreshToken) } returns userId
            every { refreshTokenStore.findByUserId(userId) } returns oldRefreshToken

            every { tokenProvider.generateAccessToken(userId) } returns newAccessToken
            every { tokenProvider.generateRefreshToken(userId) } returns newRefreshToken
            every { tokenProvider.refreshTokenTtlSeconds() } returns 3600

            every {
                refreshTokenStore.save(userId, newRefreshToken, 3600)
            } just Runs

            // when
            val result = handler.refreshToken(oldRefreshToken)

            // then
            result.accessToken shouldBe newAccessToken
            result.refreshToken shouldBe newRefreshToken
        }

        test("저장된 refresh token이 없으면 InvalidRefreshTokenException 발생") {
            every { tokenProvider.parseRefreshToken(any()) } returns 1L
            every { refreshTokenStore.findByUserId(1L) } returns null

            shouldThrow<InvalidRefreshTokenException> {
                handler.refreshToken("any-token")
            }
        }

        test("저장된 refresh token과 요청 토큰이 다르면 InvalidRefreshTokenException 발생") {
            every { tokenProvider.parseRefreshToken(any()) } returns 1L
            every { refreshTokenStore.findByUserId(1L) } returns "stored-token"

            shouldThrow<InvalidRefreshTokenException> {
                handler.refreshToken("different-token")
            }
        }
    })
