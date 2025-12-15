package com.nextmall.bff.integration.user

import com.nextmall.bff.integration.user.request.CreateUserRequest
import com.nextmall.bff.integration.user.response.CreateUserResponse
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class UserServiceClientImpl(
    webClientFactory: WebClientFactory,
    properties: UserIntegrationProperties,
) : UserServiceClient {
    private val client = webClientFactory.create(properties.baseUrl)

    override suspend fun createUser(
        nickname: String,
        email: String?,
    ): Long =
        client
            .post()
            .uri(USER_INTERNAL_URI)
            .bodyValue(CreateUserRequest(nickname, email))
            .retrieve()
            .awaitBody<CreateUserResponse>()
            .userId

    override suspend fun activateUser(userId: Long) {
        client
            .post()
            .uri("$USER_INTERNAL_URI/$userId/activate")
            .retrieve()
            .awaitBody<Unit>()
    }

    override suspend fun markSignupFailed(userId: Long) {
        client
            .post()
            .uri("$USER_INTERNAL_URI/$userId/signup-failed")
            .retrieve()
            .awaitBody<Unit>()
    }

    companion object {
        private const val USER_INTERNAL_URI = "/internal/api/v1/users"
    }
}
