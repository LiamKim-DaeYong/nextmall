package com.nextmall.authservice.presentation.request

import com.nextmall.auth.domain.account.AuthProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateAuthAccountRequest(
    @field:NotNull
    val userId: Long,

    val provider: AuthProvider,

    @field:NotBlank
    val providerAccountId: String,

    val password: String?,
)
