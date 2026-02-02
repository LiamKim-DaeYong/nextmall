package com.nextmall.orchestrator.client.auth

import com.nextmall.orchestrator.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.orchestrator.client.auth.request.IssueTokenClientRequest
import com.nextmall.orchestrator.client.auth.response.CreateAuthAccountClientResponse
import com.nextmall.orchestrator.client.auth.response.TokenClientResponse
import com.nextmall.common.web.mvc.client.RestClientFactory
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.body

@Component
class AuthServiceClient(
    restClientFactory: RestClientFactory,
    properties: AuthServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) {
    private val client =
        restClientFactory.create(
            baseUrl = properties.baseUrl,
            interceptor = passportTokenPropagationInterceptor,
        )

    fun createAccount(
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
                .body<CreateAuthAccountClientResponse>()

        return checkNotNull(response) {
            "Auth service returned null response for createAccount"
        }.authAccountId
    }

    fun issueToken(
        authAccountId: Long,
    ): TokenClientResponse {
        val response =
            client
                .post()
                .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
                .body(IssueTokenClientRequest(authAccountId))
                .retrieve()
                .body<TokenClientResponse>()

        return checkNotNull(response) {
            "Auth service returned null response for issueToken"
        }
    }

    companion object {
        private const val AUTH_INTERNAL_CREATE_ACCOUNT_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens/issue"
    }
}
