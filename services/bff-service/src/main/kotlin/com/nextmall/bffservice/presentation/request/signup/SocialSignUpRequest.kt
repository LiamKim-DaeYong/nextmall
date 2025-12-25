package com.nextmall.bffservice.presentation.request.signup

import com.nextmall.bff.client.auth.AuthProvider

data class SocialSignUpRequest(
    val provider: AuthProvider,
    val authCode: String,
    val nickname: String,
)
