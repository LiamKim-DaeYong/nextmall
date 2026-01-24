package com.nextmall.bff.client.orchestrator.request

data class CreateOrderOrchestrationClientRequest(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
