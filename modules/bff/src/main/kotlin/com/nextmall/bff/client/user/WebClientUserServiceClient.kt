package com.nextmall.bff.client.user

import com.nextmall.bff.client.user.request.CreateUserClientRequest
import com.nextmall.bff.client.user.response.CreateUserClientResponse
import com.nextmall.bff.client.user.response.UserViewClientResponse
import com.nextmall.bff.security.ServiceWebClientFactory
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class WebClientUserServiceClient(
    serviceWebClientFactory: ServiceWebClientFactory,
    properties: UserServiceClientProperties,
) : UserServiceClient {
    private val client = serviceWebClientFactory.create(properties.baseUrl, TARGET_SERVICE)

    override suspend fun getUser(
        userId: Long,
    ): UserViewClientResponse =
        client
            .get()
            .uri(USER_GET_URI, userId)
            .retrieve()
            .awaitBody<UserViewClientResponse>()

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
            .toBodilessEntity()
            .awaitSingle()
    }

    override suspend fun markSignupFailed(userId: Long) {
        client
            .post()
            .uri(USER_SIGNUP_FAIL_URI, userId)
            .retrieve()
            .toBodilessEntity()
            .awaitSingle()
    }

    companion object {
        private const val TARGET_SERVICE = "user-service"
        private const val USER_GET_URI = "/users/{id}"
        private const val USER_CREATE_URI = "/users"
        private const val USER_ACTIVATE_URI = "/users/{id}/activate"
        private const val USER_SIGNUP_FAIL_URI = "/users/{id}/signup-failed"
    }
}
