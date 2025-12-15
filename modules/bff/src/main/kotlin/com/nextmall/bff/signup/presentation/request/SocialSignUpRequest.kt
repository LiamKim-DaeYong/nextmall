package com.nextmall.bff.signup.presentation.request

import com.nextmall.bff.integration.auth.AuthProvider

data class SocialSignUpRequest(
    val provider: AuthProvider,
    val authCode: String,
    val nickname: String,
)
