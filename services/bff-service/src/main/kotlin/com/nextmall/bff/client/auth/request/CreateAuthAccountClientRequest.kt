package com.nextmall.bff.client.auth.request

import com.nextmall.bff.client.auth.AuthProvider

data class CreateAuthAccountClientRequest(
    val userId: Long,
    val provider: AuthProvider,
    val providerAccountId: String,
    val password: String?,
)
