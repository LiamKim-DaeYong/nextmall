package com.nextmall.bff.client.orchestrator

import com.nextmall.bff.client.auth.AuthProvider
import com.nextmall.bff.client.orchestrator.response.CreateOrderOrchestrationClientResponse
import com.nextmall.bff.client.orchestrator.response.SignUpOrchestrationClientResponse
import reactor.core.publisher.Mono

interface OrchestratorServiceClient {
    fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
    ): Mono<CreateOrderOrchestrationClientResponse>

    fun signUp(
        provider: AuthProvider,
        providerAccountId: String,
        password: String?,
        nickname: String,
    ): Mono<SignUpOrchestrationClientResponse>
}
