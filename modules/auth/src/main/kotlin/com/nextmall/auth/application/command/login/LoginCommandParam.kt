package com.nextmall.auth.application.command.login

import com.nextmall.auth.domain.model.AuthProvider

data class LoginCommandParam(
    val provider: AuthProvider,
    val principal: String,
    val credential: String?,
)
