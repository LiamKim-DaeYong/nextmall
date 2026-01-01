package com.nextmall.authservice.presentation.request.token

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank
    val refreshToken: String,
)
