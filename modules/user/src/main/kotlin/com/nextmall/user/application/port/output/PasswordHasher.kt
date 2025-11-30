package com.nextmall.user.application.port.output

fun interface PasswordHasher {
    fun encode(raw: String): String
}
