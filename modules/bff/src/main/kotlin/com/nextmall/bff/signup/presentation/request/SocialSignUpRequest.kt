package com.nextmall.bff.signup.presentation.request

import com.nextmall.auth.domain.model.AuthProvider

data class SocialSignUpRequest(
    val provider: AuthProvider,
    val authCode: String,
    val nickname: String,
)
