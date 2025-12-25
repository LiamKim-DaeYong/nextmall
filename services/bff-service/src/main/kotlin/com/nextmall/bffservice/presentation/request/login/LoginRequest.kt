package com.nextmall.bffservice.presentation.request.login

import com.nextmall.bff.application.auth.login.LoginCommand
import com.nextmall.bff.client.auth.AuthProvider
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    val provider: AuthProvider,

    @field:NotBlank
    val principal: String,

    val credential: String?,
)

fun LoginRequest.toCommand() =
    LoginCommand(
        provider = provider,
        principal = principal,
        credential = credential,
    )
