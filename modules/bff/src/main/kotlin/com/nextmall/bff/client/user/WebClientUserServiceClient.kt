package com.nextmall.bff.client.user

import com.nextmall.bff.client.user.request.CreateUserClientRequest
import com.nextmall.bff.client.user.response.CreateUserClientResponse
import com.nextmall.bff.client.user.response.UserViewClientResult
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class WebClientUserServiceClient(
    webClientFactory: WebClientFactory,
    properties: UserServiceClientProperties,
) : UserServiceClient {
    private val client = webClientFactory.create(properties.baseUrl)

    override suspend fun getUser(
        userId: Long,
    ): UserViewClientResult =
        client
            .get()
            .uri(USER_GET_URI, userId)
            .retrieve()
            .awaitBody<UserViewClientResult>()

    override suspend fun createUser(
        nickname: String,
        email: String?,
    ): Long =
        client
            .post()
            .uri(USER_CREATE_URI)
            .bodyValue(CreateUserClientRequest(nickname, email))
            .retrieve()
            .awaitBody<CreateUserClientResponse>()
            .userId

    override suspend fun activateUser(userId: Long) {
        client
            .post()
            .uri(USER_ACTIVATE_URI, userId)
            .retrieve()
            .awaitBody<Unit>()
    }

    override suspend fun markSignupFailed(userId: Long) {
        client
            .post()
            .uri(USER_SIGNUP_FAIL_URI, userId)
            .retrieve()
            .awaitBody<Unit>()
    }

    companion object {
        private const val USER_GET_URI = "/users/{id}"
        private const val USER_CREATE_URI = "/users"
        private const val USER_ACTIVATE_URI = "/users/{id}/activate"
        private const val USER_SIGNUP_FAIL_URI = "/users/{id}/signup-failed"
    }
}
