package com.nextmall.auth.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nextmall.auth.application.usecase.LoginUseCase
import com.nextmall.auth.application.usecase.RefreshTokenUseCase
import com.nextmall.auth.presentation.dto.LoginRequest
import com.nextmall.auth.presentation.dto.RefreshTokenRequest
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.common.testsupport.PresentationTest
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@PresentationTest(AuthController::class)
class AuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean
    private val loginUseCase: LoginUseCase,
    @MockkBean
    private val refreshTokenUseCase: RefreshTokenUseCase,
) : FunSpec({

        test("로그인 요청이 성공하면 토큰이 반환된다") {
            // given
            val request =
                LoginRequest(
                    email = "user@example.com",
                    password = "password",
                )

            val response =
                TokenResponse(
                    accessToken = "accessToken123",
                    refreshToken = "refreshToken123",
                )

            every { loginUseCase.login(any(), any()) } returns response

            // when & then
            mockMvc
                .post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") {
                        value("accessToken123")
                    }
                    jsonPath("$.refreshToken") {
                        value("refreshToken123")
                    }
                }
        }

        test("refresh 성공 시 새 accessToken, refreshToken 반환") {
            // given
            val request =
                RefreshTokenRequest(
                    refreshToken = "old-refresh-token",
                )

            val response =
                TokenResponse(
                    accessToken = "new-access",
                    refreshToken = "new-refresh",
                )

            every { refreshTokenUseCase.refresh("old-refresh-token") } returns response

            // when & then
            mockMvc
                .post("/api/v1/auth/refresh") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.accessToken") {
                        value("new-access")
                    }
                    jsonPath("$.refreshToken") {
                        value("new-refresh")
                    }
                }
        }
    })
