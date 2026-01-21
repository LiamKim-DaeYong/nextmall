package com.nextmall.bff.client.auth

import com.nextmall.bff.client.auth.request.CreateAuthAccountClientRequest
import com.nextmall.bff.client.auth.request.IssueTokenClientRequest
import com.nextmall.bff.client.auth.request.LoginClientRequest
import com.nextmall.bff.client.auth.request.RefreshTokenClientRequest
import com.nextmall.bff.client.auth.request.RevokeTokenClientRequest
import com.nextmall.bff.client.auth.response.CreateAuthAccountClientResponse
import com.nextmall.bff.client.auth.response.TokenClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
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

    override fun login(
        provider: AuthProvider,
        principal: String,
        credential: String?,
    ): Mono<TokenClientResponse> =
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
            .bodyToMono<TokenClientResponse>()

    override fun logout(
        refreshToken: String,
    ): Mono<Void> =
        client
            .post()
            .uri(AUTH_INTERNAL_LOGOUT_URI)
            .bodyValue(RevokeTokenClientRequest(refreshToken))
            .retrieve()
            .bodyToMono<Void>()

    override fun issueToken(
        authAccountId: Long,
    ): Mono<TokenClientResponse> =
        client
            .post()
            .uri(AUTH_INTERNAL_ISSUE_TOKEN_URI)
            .bodyValue(IssueTokenClientRequest(authAccountId))
            .retrieve()
            .bodyToMono<TokenClientResponse>()

    override fun refresh(
        refreshToken: String,
    ): Mono<TokenClientResponse> =
        client
            .post()
            .uri(AUTH_INTERNAL_REFRESH_URI)
            .bodyValue(RefreshTokenClientRequest(refreshToken))
            .retrieve()
            .bodyToMono<TokenClientResponse>()

    companion object {
        private const val TARGET_SERVICE = "auth-service"
        private const val AUTH_INTERNAL_CREATE_ACCOUNT_URI = "/auth/accounts"
        private const val AUTH_INTERNAL_LOGIN_URI = "/auth/tokens/login"
        private const val AUTH_INTERNAL_LOGOUT_URI = "/auth/tokens/logout"
        private const val AUTH_INTERNAL_ISSUE_TOKEN_URI = "/auth/tokens/issue"
        private const val AUTH_INTERNAL_REFRESH_URI = "/auth/tokens/refresh"
    }
}
