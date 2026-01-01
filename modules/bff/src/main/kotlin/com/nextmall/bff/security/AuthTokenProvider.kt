package com.nextmall.bff.security

interface AuthTokenProvider {
    fun currentToken(): String?
}
