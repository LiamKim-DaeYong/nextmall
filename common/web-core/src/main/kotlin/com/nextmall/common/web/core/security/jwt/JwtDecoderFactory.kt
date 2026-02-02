package com.nextmall.common.web.core.security.jwt

import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

object JwtDecoderFactory {
    fun create(secretKey: String): JwtDecoder {
        val key = SecretKeyDecoder.decode(secretKey)
        return NimbusJwtDecoder.withSecretKey(key).build()
    }
}
