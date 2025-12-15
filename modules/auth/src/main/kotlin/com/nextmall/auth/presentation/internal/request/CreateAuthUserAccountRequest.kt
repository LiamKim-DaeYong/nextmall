package com.nextmall.auth.presentation.internal.request

import com.nextmall.auth.domain.model.AuthProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateAuthUserAccountRequest(
    @field:NotNull
    val userId: Long,

    val provider: AuthProvider,

    @field:NotBlank
    val providerAccountId: String,

    val password: String?,
)
