package com.nextmall.orchestrator.client.user

import com.nextmall.common.web.mvc.client.RestClientFactory
import com.nextmall.orchestrator.client.user.request.CreateUserClientRequest
import com.nextmall.orchestrator.client.user.response.CreateUserClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.body

@Component
class UserServiceClient(
    restClientFactory: RestClientFactory,
    properties: UserServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) {
    private val client =
        restClientFactory.create(
            baseUrl = properties.baseUrl,
            interceptor = passportTokenPropagationInterceptor,
        )

    fun createUser(
        nickname: String,
        email: String?,
    ): Long {
        val response =
            client
                .post()
                .uri(USER_CREATE_URI)
                .body(CreateUserClientRequest(nickname, email))
                .retrieve()
                .body<CreateUserClientResponse>()

        return checkNotNull(response) {
            "User service returned null response for createUser"
        }.userId
    }

    fun activateUser(userId: Long) {
        client
            .post()
            .uri(USER_ACTIVATE_URI, userId)
            .retrieve()
            .toBodilessEntity()
    }

    fun markSignupFailed(userId: Long) {
        client
            .post()
            .uri(USER_SIGNUP_FAIL_URI, userId)
            .retrieve()
            .toBodilessEntity()
    }

    companion object {
        private const val USER_CREATE_URI = "/users"
        private const val USER_ACTIVATE_URI = "/users/{id}/activate"
        private const val USER_SIGNUP_FAIL_URI = "/users/{id}/signup-failed"
    }
}
