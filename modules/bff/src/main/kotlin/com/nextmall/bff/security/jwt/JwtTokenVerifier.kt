package com.nextmall.bff.security.jwt

interface JwtTokenVerifier {
    fun verify(token: String): VerifiedJwt
}
