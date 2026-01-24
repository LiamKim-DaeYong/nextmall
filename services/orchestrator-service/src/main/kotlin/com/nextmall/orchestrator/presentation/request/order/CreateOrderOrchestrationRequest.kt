package com.nextmall.orchestrator.presentation.request.order

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CreateOrderOrchestrationRequest(
    @field:NotNull
    val userId: Long,
    @field:NotNull
    val productId: Long,
    @field:Min(1)
    val quantity: Int,
)
