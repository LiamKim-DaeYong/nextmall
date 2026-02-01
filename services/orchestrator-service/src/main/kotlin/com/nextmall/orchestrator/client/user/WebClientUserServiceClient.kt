package com.nextmall.orchestrator.client.user

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.orchestrator.client.user.request.CreateUserClientRequest
import com.nextmall.orchestrator.client.user.response.CreateUserClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationFilter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class WebClientUserServiceClient(
    webClientFactory: WebClientFactory,
    properties: UserServiceClientProperties,
) : UserServiceClient {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    override fun createUser(
        nickname: String,
        email: String?,
    ): Long {
        val response =
            client
                .post()
                .uri(USER_CREATE_URI)
                .bodyValue(CreateUserClientRequest(nickname, email))
                .retrieve()
                .bodyToMono<CreateUserClientResponse>()
                .block()

        return requireNotNull(response).userId
    }

    override fun activateUser(userId: Long) {
        client
            .post()
            .uri(USER_ACTIVATE_URI, userId)
            .retrieve()
            .bodyToMono<Void>()
            .block()
    }

    override fun markSignupFailed(userId: Long) {
        client
            .post()
            .uri(USER_SIGNUP_FAIL_URI, userId)
            .retrieve()
            .bodyToMono<Void>()
            .block()
    }

    companion object {
        private const val USER_CREATE_URI = "/users"
        private const val USER_ACTIVATE_URI = "/users/{id}/activate"
        private const val USER_SIGNUP_FAIL_URI = "/users/{id}/signup-failed"
    }
}
