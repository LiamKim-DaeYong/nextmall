package com.nextmall.common.security.jwt

import org.slf4j.LoggerFactory
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object SecretKeyDecoder {
    private val log = LoggerFactory.getLogger(SecretKeyDecoder::class.java)
    private const val HMAC_SHA256 = "HmacSHA256"

    fun decode(secretKey: String): SecretKey {
        val decoded =
            runCatching {
                Base64.getDecoder().decode(secretKey)
            }.getOrElse { e ->
                log.debug("Base64 decoding failed, using raw bytes: {}", e.message)
                secretKey.toByteArray()
            }
        return SecretKeySpec(decoded, HMAC_SHA256)
    }
}
