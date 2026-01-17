package com.nextmall.authservice.presentation.controller

import com.nextmall.auth.domain.account.AuthProvider
import com.nextmall.authservice.presentation.request.account.CreateAuthAccountRequest
import com.nextmall.authservice.presentation.response.account.CreateAuthAccountResponse
import com.nextmall.common.security.internal.ServiceTokenConstants
import com.nextmall.common.testsupport.annotation.IntegrationTest
import com.nextmall.common.testsupport.security.TestServiceTokenIssuer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@IntegrationTest
class AuthAccountControllerTest(
    private val webTestClient: WebTestClient,
    private val testServiceTokenIssuer: TestServiceTokenIssuer,
) : FunSpec({

        test("POST /auth/accounts 유효한 내부 토큰으로 요청 시 201 Created와 authAccountId를 반환한다") {
            // given
            val request =
                CreateAuthAccountRequest(
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "test@test.com",
                    password = "password123!",
                )
            val internalToken =
                testServiceTokenIssuer.issueBearerToken(
                    sourceService = "bff-service",
                    targetService = "auth-service",
                )

            // when & then
            webTestClient
                .post()
                .uri("/auth/accounts")
                .header(ServiceTokenConstants.TOKEN_HEADER, internalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated
                .expectBody<CreateAuthAccountResponse>()
                .value { response ->
                    response.shouldNotBeNull()
                    response.authAccountId.shouldBeGreaterThan(0L)
                }
        }

        test("POST /auth/accounts 내부 토큰 없이 요청 시 401 Unauthorized를 반환한다") {
            // given
            val request =
                CreateAuthAccountRequest(
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "test@test.com",
                    password = "password123!",
                )

            // when & then
            webTestClient
                .post()
                .uri("/auth/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        test("POST /auth/accounts 잘못된 내부 토큰으로 요청 시 401 Unauthorized를 반환한다") {
            // given
            val request =
                CreateAuthAccountRequest(
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "test@test.com",
                    password = "password123!",
                )
            val invalidToken = "Bearer invalid-token"

            // when & then
            webTestClient
                .post()
                .uri("/auth/accounts")
                .header(ServiceTokenConstants.TOKEN_HEADER, invalidToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

        test("POST /auth/accounts 중복된 providerAccountId로 요청 시 409 Conflict를 반환한다") {
            // given
            val request =
                CreateAuthAccountRequest(
                    userId = 1L,
                    provider = AuthProvider.LOCAL,
                    providerAccountId = "existing@test.com", // 이미 존재하는 계정
                    password = "password123!",
                )
            val internalToken =
                testServiceTokenIssuer.issueBearerToken(
                    sourceService = "bff-service",
                    targetService = "auth-service",
                )

            // 계정 생성
            webTestClient
                .post()
                .uri("/auth/accounts")
                .header(ServiceTokenConstants.TOKEN_HEADER, internalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated

            // when & then - 동일한 계정으로 재요청
            webTestClient
                .post()
                .uri("/auth/accounts")
                .header(ServiceTokenConstants.TOKEN_HEADER, internalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(409) // Conflict
        }
    })
