package com.nextmall.common.security.jwt

import io.jsonwebtoken.security.Keys
import java.util.Base64
import javax.crypto.SecretKey

object SecretKeyDecoder {
    fun decode(secretKey: String): SecretKey {
        val decoded =
            runCatching {
                Base64.getDecoder().decode(secretKey)
            }.getOrElse {
                secretKey.toByteArray()
            }
        return Keys.hmacShaKeyFor(decoded)
    }
}
