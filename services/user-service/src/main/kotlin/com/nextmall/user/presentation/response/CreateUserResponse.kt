package com.nextmall.user.presentation.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class CreateUserResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val userId: Long,
)
