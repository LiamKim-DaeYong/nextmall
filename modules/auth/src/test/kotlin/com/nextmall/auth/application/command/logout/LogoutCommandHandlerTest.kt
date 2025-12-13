package com.nextmall.auth.application.command.logout

import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.auth.port.output.token.TokenProvider
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LogoutCommandHandlerTest :
    FunSpec({

        val tokenProvider = mockk<TokenProvider>()
        val store = mockk<RefreshTokenStore>()

        lateinit var handler: LogoutCommandHandler

        beforeTest {
            clearMocks(tokenProvider, store)
            handler = LogoutCommandHandler(tokenProvider, store)
        }

        test("로그아웃 시 refresh token이 정상적으로 삭제된다") {
            every { tokenProvider.parseRefreshToken("rt") } returns 1L
            every { store.delete(1L) } returns true

            handler.logout("rt")

            verify(exactly = 1) {
                store.delete(1L)
            }
        }

        test("유효하지 않은 refresh token이어도 예외를 던지지 않고 저장소를 건드리지 않는다") {
            every {
                tokenProvider.parseRefreshToken("invalid-rt")
            } throws IllegalArgumentException("Invalid token")

            handler.logout("invalid-rt")

            verify(exactly = 0) {
                store.delete(any())
            }
        }
    })
