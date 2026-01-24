package com.nextmall.orchestrator.client.auth

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.orchestrator.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.orchestrator.client.auth.request.IssueTokenClientRequest
import com.nextmall.orchestrator.client.auth.response.CreateAuthAccountClientResponse
import com.nextmall.orchestrator.client.auth.response.TokenClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationFilter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class WebClientAuthServiceClient(
    webClientFactory: WebClientFactory,
    properties: AuthServiceClientProperties,
) : AuthServiceClient {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    override fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Mono<Long> =
        client
            .post()
            .uri(AUTH_INTERNAL_CREATE_ACCOUNT_URI)
            .bodyValue(
                CreateAuthAccountClientRequest(
                    userId = userId,
                    provider = provider,
                    providerAccountId = providerAccountId,
                    password = password,
                ),
            ).retrieve()
            .bodyToMono<CreateAuthAccountClientResponse>()
            .map { it.authAccountId }

    override fun issueToken(
        authAccountId: Long,
    ): Mono<TokenClientResponse> =
        client
            .post()
            .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
            .bodyValue(IssueTokenClientRequest(authAccountId))
            .retrieve()
            .bodyToMono<TokenClientResponse>()

    companion object {
        private const val AUTH_INTERNAL_CREATE_ACCOUNT_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens/issue"
    }
}
