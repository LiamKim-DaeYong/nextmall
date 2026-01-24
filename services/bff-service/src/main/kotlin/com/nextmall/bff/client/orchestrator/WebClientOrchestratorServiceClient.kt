package com.nextmall.bff.client.orchestrator

import com.nextmall.bff.client.auth.AuthProvider
import com.nextmall.bff.client.orchestrator.request.CreateOrderOrchestrationClientRequest
import com.nextmall.bff.client.orchestrator.request.SignUpOrchestrationClientRequest
import com.nextmall.bff.client.orchestrator.response.CreateOrderOrchestrationClientResponse
import com.nextmall.bff.client.orchestrator.response.SignUpOrchestrationClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class WebClientOrchestratorServiceClient(
    webClientFactory: WebClientFactory,
    properties: OrchestratorServiceClientProperties,
) : OrchestratorServiceClient {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    override fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
    ): Mono<CreateOrderOrchestrationClientResponse> =
        client
            .post()
            .uri(ORDER_ORCHESTRATION_URI)
            .bodyValue(CreateOrderOrchestrationClientRequest(userId, productId, quantity))
            .retrieve()
            .bodyToMono<CreateOrderOrchestrationClientResponse>()

    override fun signUp(
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
        nickname: String,
    ): Mono<SignUpOrchestrationClientResponse> =
        client
            .post()
            .uri(SIGN_UP_ORCHESTRATION_URI)
            .bodyValue(
                SignUpOrchestrationClientRequest(
                    provider = provider,
                    providerAccountId = providerAccountId,
                    password = password,
                    nickname = nickname,
                ),
            ).retrieve()
            .bodyToMono<SignUpOrchestrationClientResponse>()

    companion object {
        private const val ORDER_ORCHESTRATION_URI = "/orchestrations/orders"
        private const val SIGN_UP_ORCHESTRATION_URI = "/orchestrations/sign-up"
    }
}
