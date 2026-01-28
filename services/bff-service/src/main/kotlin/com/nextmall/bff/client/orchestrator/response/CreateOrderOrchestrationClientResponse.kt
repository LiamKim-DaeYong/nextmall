package com.nextmall.bff.client.orchestrator.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateOrderOrchestrationClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val orderId: Long,
)
