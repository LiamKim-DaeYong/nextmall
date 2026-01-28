package com.nextmall.bff.client.auth.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateAuthAccountClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val authAccountId: Long,
)
