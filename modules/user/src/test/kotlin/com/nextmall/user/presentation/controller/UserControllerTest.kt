package com.nextmall.user.presentation.controller

import com.nextmall.common.testsupport.WebMvcTestSupport
import com.nextmall.user.application.query.UserContext
import com.nextmall.user.port.input.FindUserQuery
import com.nextmall.user.presentation.mapper.UserResponseMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.OffsetDateTime

@WebMvcTestSupport
@WebMvcTest(UserController::class)
@Import(UserResponseMapper::class)
class UserControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean
    private val findUserQuery: FindUserQuery,
) : FunSpec({
        test("ID로 사용자 조회 시 PublicUserResponse가 반환된다") {
            // given
            val userContext =
                UserContext(
                    id = 1L,
                    email = "find@example.com",
                    nickname = "finder",
                    createdAt = OffsetDateTime.now(),
                )

            every { findUserQuery.findById(1L) } returns userContext

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
