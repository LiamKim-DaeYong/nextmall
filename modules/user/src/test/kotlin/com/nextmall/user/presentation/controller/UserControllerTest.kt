package com.nextmall.user.presentation.controller

import com.nextmall.common.testsupport.WebMvcTestSupport
import com.nextmall.user.application.usecase.FindUserUseCase
import com.nextmall.user.application.usecase.RegisterUserUseCase
import com.nextmall.user.domain.model.UserRole
import com.nextmall.user.presentation.dto.RegisterUserRequest
import com.nextmall.user.presentation.dto.RegisterUserResponse
import com.nextmall.user.presentation.dto.UserResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime

@WebMvcTestSupport
@WebMvcTest(UserController::class)
class UserControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean
    private val registerUserUseCase: RegisterUserUseCase,
    @MockkBean
    private val findUserUseCase: FindUserUseCase,
) : FunSpec({

        test("회원가입 요청이 성공하면 201 Created와 응답을 반환한다") {
            // given
            val request =
                RegisterUserRequest(
                    email = "new@example.com",
                    password = "pass1234",
                    nickname = "newbie",
                )

            val response =
                RegisterUserResponse(
                    id = 1L,
                    email = "new@example.com",
                    nickname = "newbie",
                    createdAt = OffsetDateTime.now(),
                )

            every {
                registerUserUseCase.register(
                    email = request.email,
                    password = request.password,
                    nickname = request.nickname,
                )
            } returns response

            // when & then
            mockMvc
                .post("/api/v1/users") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.email") {
                        value("new@example.com")
                    }
                    jsonPath("$.nickname") {
                        value("newbie")
                    }
                }
        }

        test("ID로 사용자 조회 시 유저 정보가 반환된다") {
            // given
            val response =
                UserResponse(
                    id = 1L,
                    email = "find@example.com",
                    nickname = "finder",
                    role = UserRole.BUYER.name,
                )

            every { findUserUseCase.findById(1L) } returns response

            // when & then
            mockMvc
                .get("/api/v1/users/1")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.email") {
                        value("find@example.com")
                    }
                    jsonPath("$.nickname") {
                        value("finder")
                    }
                }
        }
    })
