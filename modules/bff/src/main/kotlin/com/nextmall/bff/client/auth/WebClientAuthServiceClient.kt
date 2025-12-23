package com.nextmall.bff.client.auth

import com.nextmall.bff.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.bff.client.auth.request.IssueTokenClientRequest
import com.nextmall.bff.client.auth.response.TokenClientResult
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class WebClientAuthServiceClient(
    webClientFactory: WebClientFactory,
    properties: AuthServiceClientProperties,
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
                CreateAuthAccountClientRequest(
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
    ): TokenClientResult =
        client
            .post()
            .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
            .bodyValue(IssueTokenClientRequest(userId))
            .retrieve()
            .awaitBody()

    companion object {
        private const val AUTH_INTERNAL_CREATE_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens"
    }
}
