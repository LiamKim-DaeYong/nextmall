package com.nextmall.bff.client.user.response

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class UserViewClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val nickname: String,
    val email: String?,
)
