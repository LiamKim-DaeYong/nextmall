package com.nextmall.bff.signup.presentation.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LocalSignUpRequest(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(min = 8, max = 64)
    val password: String,

    @field:NotBlank
    @field:Size(min = 2, max = 20)
    val nickname: String,
)
