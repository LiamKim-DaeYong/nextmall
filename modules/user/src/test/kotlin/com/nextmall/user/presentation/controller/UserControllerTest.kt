package com.nextmall.user.presentation.controller

import com.nextmall.common.testsupport.WebMvcTestSupport
import com.nextmall.user.application.command.RegisterUserCommand
import com.nextmall.user.application.query.FindUserQuery
import com.nextmall.user.application.query.dto.UserView
import com.nextmall.user.application.command.dto.RegisterUserResult
import com.nextmall.user.domain.entity.UserRole
import com.nextmall.user.presentation.dto.request.RegisterUserRequest
import com.nextmall.user.presentation.mapper.UserResponseMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import java.time.OffsetDateTime

@WebMvcTestSupport
@WebMvcTest(UserController::class)
@Import(UserResponseMapper::class)
class UserControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean
    private val registerUserCommand: RegisterUserCommand,
    @MockkBean
    private val findUserQuery: FindUserQuery,
) : FunSpec({

        test("회원가입 요청이 성공하면 201 Created와 응답을 반환한다") {
            // given
            val request =
                RegisterUserRequest(
                    email = "new@example.com",
                    password = "pass1234",
                    nickname = "newbie",
                )

            val result =
                RegisterUserResult(
                    id = 1L,
                    email = "new@example.com",
                    nickname = "newbie",
                    createdAt = OffsetDateTime.now(),
                )

            every {
                registerUserCommand.register(
                    email = request.email,
                    password = request.password,
                    nickname = request.nickname,
                )
            } returns result

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

        test("ID로 사용자 조회 시 PublicUserResponse가 반환된다") {
            // given
            val userView =
                UserView(
                    id = 1L,
                    email = "find@example.com",
                    nickname = "finder",
                    role = UserRole.BUYER.name,
                    provider = "local",
                    createdAt = OffsetDateTime.now(),
                )

            every { findUserQuery.findById(1L) } returns userView

            // when & then
            mockMvc
                .get("/api/v1/users/1")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(1L) }
                    jsonPath("$.nickname") { value("finder") }
                }
        }
    })
