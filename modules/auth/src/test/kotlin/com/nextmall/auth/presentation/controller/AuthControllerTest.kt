package com.nextmall.auth.presentation.controller

import com.nextmall.auth.application.command.logout.LogoutCommandHandler
import com.nextmall.auth.application.token.TokenResult
import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.auth.port.input.login.LoginCommand
import com.nextmall.auth.port.input.token.RefreshTokenCommand
import com.nextmall.auth.presentation.public.mapper.AuthResponseMapper
import com.nextmall.auth.presentation.public.controller.AuthController
import com.nextmall.auth.presentation.public.request.LoginRequest
import com.nextmall.auth.presentation.public.request.RefreshTokenRequest
import com.nextmall.auth.presentation.public.response.TokenResponse
import com.nextmall.common.testsupport.WebMvcTestSupport
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper

@WebMvcTestSupport
@WebMvcTest(AuthController::class)
class AuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val loginCommand: LoginCommand,
    @MockkBean private val refreshTokenCommand: RefreshTokenCommand,
    @MockkBean private val logoutCommandHandler: LogoutCommandHandler,
    @MockkBean private val mapper: AuthResponseMapper,
) : FunSpec({

        test("로그인 요청이 성공하면 토큰이 반환된다") {
            // given
            val request =
                LoginRequest(
                    provider = AuthProvider.LOCAL,
                    principal = "user@example.com",
                    credential = "password",
                )

            val commandResult =
                TokenResult(
                    accessToken = "accessToken123",
                    refreshToken = "refreshToken123",
                )

            val response =
                TokenResponse(
                    accessToken = "accessToken123",
                    refreshToken = "refreshToken123",
                )

            every { loginCommand.login(any()) } returns commandResult
            every { mapper.toTokenResponse(commandResult) } returns response

            // when & then
            mockMvc
                .post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { value("accessToken123") }
                    jsonPath("$.refreshToken") { value("refreshToken123") }
                }
        }

        test("refresh 성공 시 새 accessToken, refreshToken 반환") {
            // given
            val request = RefreshTokenRequest(refreshToken = "old-refresh-token")

            val commandResult =
                TokenResult(
                    accessToken = "new-access",
                    refreshToken = "new-refresh",
                )

            val response =
                TokenResponse(
                    accessToken = "new-access",
                    refreshToken = "new-refresh",
                )

            every { refreshTokenCommand.refreshToken("old-refresh-token") } returns commandResult
            every { mapper.toTokenResponse(commandResult) } returns response

            // when & then
            mockMvc
                .post("/api/v1/auth/refresh") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") { value("new-access") }
                    jsonPath("$.refreshToken") { value("new-refresh") }
                }
        }

        test("로그아웃 성공 시 200을 반환한다") {
            val request = RefreshTokenRequest("old-rt")

            every { logoutCommandHandler.logout("old-rt") } returns Unit

            mockMvc
                .post("/api/v1/auth/logout") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                }
        }
    })
