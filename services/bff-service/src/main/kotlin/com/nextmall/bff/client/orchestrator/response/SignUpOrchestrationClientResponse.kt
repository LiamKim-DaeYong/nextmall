package com.nextmall.bff.client.orchestrator.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class SignUpOrchestrationClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
