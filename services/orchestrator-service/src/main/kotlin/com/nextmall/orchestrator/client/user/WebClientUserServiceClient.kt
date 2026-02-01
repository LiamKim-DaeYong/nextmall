package com.nextmall.orchestrator.client.user

import com.nextmall.orchestrator.client.user.request.CreateUserClientRequest
import com.nextmall.orchestrator.client.user.response.CreateUserClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class WebClientUserServiceClient(
    restClientBuilder: RestClient.Builder,
    properties: UserServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) : UserServiceClient {
    private val client =
        restClientBuilder
            .baseUrl(properties.baseUrl)
            .requestInterceptor(passportTokenPropagationInterceptor)
            .build()

    override fun createUser(
        nickname: String,
        email: String?,
    ): Long {
        val response =
            client
                .post()
                .uri(USER_CREATE_URI)
                .body(CreateUserClientRequest(nickname, email))
                .retrieve()
                .body(CreateUserClientResponse::class.java)

        return requireNotNull(response).userId
    }

    override fun activateUser(userId: Long) {
        client
            .post()
            .uri(USER_ACTIVATE_URI, userId)
            .retrieve()
            .toBodilessEntity()
    }

    override fun markSignupFailed(userId: Long) {
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
