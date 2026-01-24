package com.nextmall.orchestrator.client.auth.request

import com.nextmall.orchestrator.client.auth.AuthProvider

data class CreateAuthAccountClientRequest(
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String?,
)
