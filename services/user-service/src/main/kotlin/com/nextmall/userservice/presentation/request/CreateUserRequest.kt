package com.nextmall.userservice.presentation.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:NotBlank
    val nickname: String,

    @field:Email
    val email: String?,
)
