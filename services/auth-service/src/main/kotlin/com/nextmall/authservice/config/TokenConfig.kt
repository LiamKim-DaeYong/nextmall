package com.nextmall.authservice.config

import com.nextmall.auth.config.ExternalTokenProperties
import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.passport")
class PassportTokenProperties(
    override val secretKey: String,
) : ServiceTokenProperties

@Configuration
@EnableConfigurationProperties(ExternalTokenProperties::class, PassportTokenProperties::class)
class TokenConfig
