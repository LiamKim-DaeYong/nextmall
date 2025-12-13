package com.nextmall.bff.signup.presentation.request

data class LocalSignUpRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
