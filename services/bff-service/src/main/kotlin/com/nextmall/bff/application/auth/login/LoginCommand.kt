package com.nextmall.bff.application.auth.login

import com.nextmall.bff.client.auth.AuthProvider

data class LoginCommand(
    val provider: AuthProvider,
    val principal: String,
    val credential: String?,
)
