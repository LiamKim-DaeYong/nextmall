package com.nextmall.common.security.jwt

import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import java.util.Base64
import javax.crypto.SecretKey

object SecretKeyDecoder {
    private val log = LoggerFactory.getLogger(SecretKeyDecoder::class.java)

    fun decode(secretKey: String): SecretKey {
        val decoded =
            runCatching {
                Base64.getDecoder().decode(secretKey)
            }.getOrElse { e ->
                log.debug("Base64 decoding failed, using raw bytes: {}", e.message)
                secretKey.toByteArray()
            }
        return Keys.hmacShaKeyFor(decoded)
    }
}
