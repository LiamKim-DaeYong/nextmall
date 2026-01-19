package com.nextmall.auth.presentation.request.account

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
