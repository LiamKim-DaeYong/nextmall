package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.exception.InvalidRefreshTokenException
import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import com.nextmall.user.application.port.output.UserCredentialsQueryRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk

class RefreshTokenUseCaseTest :
    FunSpec({
        val tokenProvider = mockk<TokenProvider>()
        val refreshTokenStore = mockk<RefreshTokenStore>()
        val credentialsRepository = mockk<UserCredentialsQueryRepository>()

        lateinit var useCase: RefreshTokenUseCase

        beforeTest {
            clearMocks(tokenProvider, refreshTokenStore, credentialsRepository)
            useCase = RefreshTokenUseCase(tokenProvider, refreshTokenStore, credentialsRepository)
        }

        test("refresh 성공 시 새 토큰 반환") {
            // given
            val refreshToken = "valid-refresh"
            val userId = 1L
            val newAccess = "new-access"
            val newRefresh = "new-refresh"

            every { tokenProvider.parseRefreshToken(refreshToken) } returns userId
            every { refreshTokenStore.findByUserId(userId) } returns refreshToken
            every { tokenProvider.generateAccessToken("$userId", any()) } returns newAccess
            every { tokenProvider.generateRefreshToken("$userId") } returns newRefresh
            every { tokenProvider.refreshTokenTtlSeconds() } returns 3600
            every { refreshTokenStore.save(userId, newRefresh, any()) } just Runs
            every { credentialsRepository.findById(userId) } returns mockk(relaxed = true)

            // when
            val result = useCase.refresh(refreshToken)

            // then
            result.accessToken shouldBe newAccess
            result.refreshToken shouldBe newRefresh
        }

        test("저장된 refresh token 없으면 실패") {
            every { tokenProvider.parseRefreshToken(any()) } returns 1L
            every { refreshTokenStore.findByUserId(1L) } returns null

            shouldThrow<InvalidRefreshTokenException> {
                useCase.refresh("x")
            }
        }

        test("저장된 refresh token과 다르면 실패") {
            every { tokenProvider.parseRefreshToken(any()) } returns 1L
            every { refreshTokenStore.findByUserId(1L) } returns "expected-token"

            shouldThrow<InvalidRefreshTokenException> {
                useCase.refresh("different-token")
            }
        }
    })
