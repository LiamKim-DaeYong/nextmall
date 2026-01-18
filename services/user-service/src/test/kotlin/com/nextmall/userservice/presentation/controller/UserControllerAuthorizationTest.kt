package com.nextmall.userservice.presentation.controller

import com.nextmall.common.security.internal.PassportTokenConstants
import com.nextmall.common.testsupport.annotation.IntegrationTest
import com.nextmall.common.testsupport.security.TestPassportTokenIssuer
import com.nextmall.user.application.UserService
import com.nextmall.user.application.query.UserView
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.test.web.reactive.server.WebTestClient

@IntegrationTest
class UserControllerAuthorizationTest(
    private val webTestClient: WebTestClient,
    private val testPassportTokenIssuer: TestPassportTokenIssuer,
    @MockkBean
    private val userService: UserService,
) : FunSpec() {
    init {
        context("GET /users/{userId} 인가 테스트") {

            test("관리자는 모든 사용자 조회 가능") {
                // given
                val targetUserId = 999L
                every { userService.getUser(targetUserId) } returns
                    UserView(
                        id = targetUserId,
                        nickname = "target-user",
                        email = "target@test.com",
                    )

                val adminToken =
                    testPassportTokenIssuer.issueBearerToken(
                        userId = "1",
                        roles = setOf("ADMIN"),
                    )

                // when & then
                webTestClient
                    .get()
                    .uri("/users/$targetUserId")
                    .header(PassportTokenConstants.HEADER_NAME, adminToken)
                    .exchange()
                    .expectStatus()
                    .isOk
            }

            test("일반 사용자는 본인 정보만 조회 가능") {
                // given
                val myUserId = 123L
                every { userService.getUser(myUserId) } returns
                    UserView(
                        id = myUserId,
                        nickname = "my-user",
                        email = "my@test.com",
                    )

                val userToken =
                    testPassportTokenIssuer.issueBearerToken(
                        userId = myUserId.toString(),
                        roles = setOf("USER"),
                    )

                // when & then
                webTestClient
                    .get()
                    .uri("/users/$myUserId")
                    .header(PassportTokenConstants.HEADER_NAME, userToken)
                    .exchange()
                    .expectStatus()
                    .isOk
            }

            test("일반 사용자는 타인 정보 조회 시 403 Forbidden") {
                // given
                val otherUserId = 999L

                val userToken =
                    testPassportTokenIssuer.issueBearerToken(
                        userId = "123",
                        roles = setOf("USER"),
                    )

                // when & then
                webTestClient
                    .get()
                    .uri("/users/$otherUserId")
                    .header(PassportTokenConstants.HEADER_NAME, userToken)
                    .exchange()
                    .expectStatus()
                    .isForbidden
            }

            test("인증되지 않은 사용자는 401 Unauthorized") {
                // given - no token

                // when & then
                webTestClient
                    .get()
                    .uri("/users/123")
                    .exchange()
                    .expectStatus()
                    .isUnauthorized
            }
        }
    }
}
