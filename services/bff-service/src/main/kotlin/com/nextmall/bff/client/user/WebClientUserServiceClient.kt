package com.nextmall.bff.client.user

import com.nextmall.bff.client.user.request.CreateUserClientRequest
import com.nextmall.bff.client.user.response.CreateUserClientResponse
import com.nextmall.bff.client.user.response.UserViewClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

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

    override fun getUser(
        userId: Long,
    ): Mono<UserViewClientResponse> =
        client
            .get()
            .uri(USER_GET_URI, userId)
            .retrieve()
            .bodyToMono<UserViewClientResponse>()

    override fun createUser(
        nickname: String,
        email: String?,
    ): Mono<Long> =
        client
            .post()
            .uri(USER_CREATE_URI)
            .bodyValue(CreateUserClientRequest(nickname, email))
            .retrieve()
            .bodyToMono<CreateUserClientResponse>()
            .map { it.userId }

    override fun activateUser(userId: Long): Mono<Void> =
        client
            .post()
            .uri(USER_ACTIVATE_URI, userId)
            .retrieve()
            .bodyToMono<Void>()

    override fun markSignupFailed(userId: Long): Mono<Void> =
        client
            .post()
            .uri(USER_SIGNUP_FAIL_URI, userId)
            .retrieve()
            .bodyToMono<Void>()

    companion object {
        private const val USER_GET_URI = "/users/{id}"
        private const val USER_CREATE_URI = "/users"
        private const val USER_ACTIVATE_URI = "/users/{id}/activate"
        private const val USER_SIGNUP_FAIL_URI = "/users/{id}/signup-failed"
    }
}
