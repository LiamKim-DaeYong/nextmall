package com.nextmall.authservice.presentation.request.token

import com.nextmall.auth.domain.account.AuthProvider
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    val provider: AuthProvider,

    @field:NotBlank
    val principal: String,

    val credential: String?,
)
