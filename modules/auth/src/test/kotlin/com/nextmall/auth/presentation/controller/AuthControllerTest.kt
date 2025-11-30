package com.nextmall.auth.presentation.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nextmall.auth.application.usecase.LoginUseCase
import com.nextmall.auth.presentation.dto.LoginRequest
import com.nextmall.auth.presentation.dto.TokenResponse
import com.nextmall.common.testsupport.PresentationTest
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@PresentationTest(AuthController::class)
class AuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean
    private val loginUseCase: LoginUseCase,
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
                .perform(
                    MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)),
                ).andExpect(
                    MockMvcResultMatchers
                        .status()
                        .isOk,
                ).andExpect(
                    MockMvcResultMatchers
                        .jsonPath("$.accessToken")
                        .value("accessToken123"),
                ).andExpect(
                    MockMvcResultMatchers
                        .jsonPath("$.refreshToken")
                        .value("refreshToken123"),
                )
        }
    })
