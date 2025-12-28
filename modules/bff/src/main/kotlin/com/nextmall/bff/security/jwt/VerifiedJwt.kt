package com.nextmall.bff.security.jwt

data class VerifiedJwt(
    val subject: String,
    val roles: List<String>,
)
