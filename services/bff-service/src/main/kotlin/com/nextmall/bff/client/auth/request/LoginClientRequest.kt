package com.nextmall.bff.client.auth.request

import com.nextmall.bff.client.auth.AuthProvider

data class LoginClientRequest(
    val provider: AuthProvider,
    val principal: String,
    val credential: String?,
)
