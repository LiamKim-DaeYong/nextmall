package com.nextmall.orchestrator.client.auth

import com.nextmall.orchestrator.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.orchestrator.client.auth.request.IssueTokenClientRequest
import com.nextmall.orchestrator.client.auth.response.CreateAuthAccountClientResponse
import com.nextmall.orchestrator.client.auth.response.TokenClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class WebClientAuthServiceClient(
    restClientBuilder: RestClient.Builder,
    properties: AuthServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) : AuthServiceClient {
    private val client =
        restClientBuilder
            .baseUrl(properties.baseUrl)
            .requestInterceptor(passportTokenPropagationInterceptor)
            .build()

    override fun createAccount(
        userId: Long,
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
    ): Long {
        val response =
            client
                .post()
                .uri(AUTH_INTERNAL_CREATE_ACCOUNT_URI)
                .body(
                    CreateAuthAccountClientRequest(
                        userId = userId,
                        provider = provider,
                        providerAccountId = providerAccountId,
                        password = password,
                    ),
                ).retrieve()
                .body(CreateAuthAccountClientResponse::class.java)

        return requireNotNull(response).authAccountId
    }

    override fun issueToken(
        authAccountId: Long,
    ): TokenClientResponse {
        val response =
            client
                .post()
                .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
                .body(IssueTokenClientRequest(authAccountId))
                .retrieve()
                .body(TokenClientResponse::class.java)

        return requireNotNull(response)
    }

    companion object {
        private const val AUTH_INTERNAL_CREATE_ACCOUNT_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens/issue"
    }
}
