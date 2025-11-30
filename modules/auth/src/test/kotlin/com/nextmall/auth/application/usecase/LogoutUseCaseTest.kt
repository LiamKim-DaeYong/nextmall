package com.nextmall.auth.application.usecase

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.domain.refresh.RefreshTokenStore
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LogoutUseCaseTest :
    FunSpec({

        val tokenProvider = mockk<TokenProvider>()
        val store = mockk<RefreshTokenStore>()

        lateinit var useCase: LogoutUseCase

        beforeTest {
            clearMocks(tokenProvider, store)
            useCase = LogoutUseCase(tokenProvider, store)
        }

        test("로그아웃 시 refresh token이 삭제된다") {
            every { tokenProvider.parseRefreshToken("rt") } returns 1L
            every { store.delete(1L) } returns true

            useCase.logout("rt")

            verify(exactly = 1) { store.delete(1L) }
        }

        test("유효하지 않은 refresh token이어도 로그아웃은 예외를 던지지 않는다") {
            every { tokenProvider.parseRefreshToken("invalid-rt") } throws IllegalArgumentException("Invalid token")

            useCase.logout("invalid-rt")
        }
    })
