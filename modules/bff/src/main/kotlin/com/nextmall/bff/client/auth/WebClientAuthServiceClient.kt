package com.nextmall.bff.client.auth

import com.nextmall.bff.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.bff.client.auth.request.IssueTokenClientRequest
import com.nextmall.bff.client.auth.request.LoginClientRequest
import com.nextmall.bff.client.auth.request.RefreshTokenClientRequest
import com.nextmall.bff.client.auth.request.RevokeTokenClientRequest
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
            .uri(AUTH_INTERNAL_CREATE_ACCOUNT_URI)
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

    override suspend fun login(
        provider: AuthProvider,
        principal: String,
        credential: String?,
    ): TokenClientResult =
        client
            .post()
            .uri(AUTH_INTERNAL_LOGIN_URI)
            .bodyValue(
                LoginClientRequest(
                    provider = provider,
                    principal = principal,
                    credential = credential,
                ),
            ).retrieve()
            .awaitBody()

    override suspend fun logout(
        refreshToken: String,
    ) {
        client
            .post()
            .uri(AUTH_INTERNAL_LOGOUT_URI)
            .bodyValue(RevokeTokenClientRequest(refreshToken))
            .retrieve()
            .awaitBody<Unit>()
    }

    override suspend fun issueTokenForUser(
        userId: Long,
    ): TokenClientResult =
        client
            .post()
            .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
            .bodyValue(IssueTokenClientRequest(userId))
            .retrieve()
            .awaitBody()

    override suspend fun refresh(
        refreshToken: String,
    ): TokenClientResult =
        client
            .post()
            .uri(AUTH_INTERNAL_REFRESH_URI)
            .bodyValue(RefreshTokenClientRequest(refreshToken))
            .retrieve()
            .awaitBody()

    companion object {
        private const val AUTH_INTERNAL_CREATE_ACCOUNT_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_LOGIN_URI = "/auth/tokens/login"
        private const val AUTH_INTERNAL_LOGOUT_URI = "/auth/tokens/logout"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens/issue"
        private const val AUTH_INTERNAL_REFRESH_URI = "/auth/tokens/refresh"
    }
}
