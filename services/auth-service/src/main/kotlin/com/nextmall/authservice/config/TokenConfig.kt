package com.nextmall.authservice.config

import com.nextmall.auth.config.ExternalTokenProperties
import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.passport")
class PassportTokenProperties(
    override val secretKey: String,
) : ServiceTokenProperties {
    init {
        val decodedKey = java.util.Base64.getDecoder().decode(secretKey)
        require(decodedKey.size >= 32) {
            "Passport token secret key must be at least 256 bits (32 bytes), but was ${decodedKey.size} bytes"
        }
    }
}

@Configuration
@EnableConfigurationProperties(ExternalTokenProperties::class, PassportTokenProperties::class)
class TokenConfig
