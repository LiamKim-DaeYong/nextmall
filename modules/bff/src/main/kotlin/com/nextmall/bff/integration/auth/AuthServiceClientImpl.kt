package com.nextmall.bff.integration.auth

import com.nextmall.bff.integration.auth.request.CreateAuthAccountRequest
import com.nextmall.bff.integration.auth.request.IssueTokenRequest
import com.nextmall.bff.integration.auth.response.TokenResult
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class AuthServiceClientImpl(
    webClientFactory: WebClientFactory,
    properties: AuthIntegrationProperties,
) : AuthServiceClient {
    private val client = webClientFactory.create(properties.baseUrl)

    override suspend fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ) {
        client
            .post()
            .uri(AUTH_INTERNAL_CREATE_URI)
            .bodyValue(
                CreateAuthAccountRequest(
                    userId = userId,
                    provider = provider,
                    providerAccountId = providerAccountId,
                    password = password,
                ),
            ).retrieve()
            .awaitBody<Unit>()
    }

    override suspend fun issueToken(
        userId: Long,
    ): TokenResult =
        client
            .post()
            .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
            .bodyValue(IssueTokenRequest(userId))
            .retrieve()
            .awaitBody()

    companion object {
        private const val AUTH_INTERNAL_CREATE_URI = "/internal/api/v1/auth/accounts"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/internal/api/v1/auth/tokens"
    }
}
