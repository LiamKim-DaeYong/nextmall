package com.nextmall.bff.integration.auth.request

import com.nextmall.bff.integration.auth.AuthProvider

data class CreateAuthAccountRequest(
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String?,
)
