package com.nextmall.auth.presentation.response.account

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateAuthAccountResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val authAccountId: Long,
)
