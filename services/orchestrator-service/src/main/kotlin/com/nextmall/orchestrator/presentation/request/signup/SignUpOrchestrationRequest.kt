package com.nextmall.orchestrator.presentation.request.signup

import com.nextmall.orchestrator.application.signup.SignUpCommand
import com.nextmall.orchestrator.client.auth.AuthProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpOrchestrationRequest(
    val provider: AuthProvider,
    @field:NotBlank
    val providerAccountId: String,
    val password: String? = null,
    @field:NotBlank
    @field:Size(min = 2, max = 20)
    val nickname: String,
)

fun SignUpOrchestrationRequest.toCommand() =
    SignUpCommand(
        provider = provider,
        providerAccountId = providerAccountId,
        password = password,
        nickname = nickname,
    )
