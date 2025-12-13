package com.nextmall.bff.signup.presentation.request

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
