package com.nextmall.bffservice.presentation.request.token

import jakarta.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank
    val refreshToken: String,
)
