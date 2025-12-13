package com.nextmall.auth.presentation.request

import com.nextmall.auth.domain.model.AuthProvider
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    val provider: AuthProvider,

    @field:NotBlank
    val principal: String,

    val credential: String?,
)
