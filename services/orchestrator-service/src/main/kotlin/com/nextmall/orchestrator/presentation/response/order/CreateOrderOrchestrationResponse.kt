package com.nextmall.orchestrator.presentation.response.order

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateOrderOrchestrationResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val orderId: Long,
)
