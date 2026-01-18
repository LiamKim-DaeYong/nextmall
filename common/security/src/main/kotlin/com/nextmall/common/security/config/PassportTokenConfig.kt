package com.nextmall.common.security.config

import com.nextmall.common.security.token.PassportTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.passport")
class PassportTokenPropertiesImpl(
    override val secretKey: String,
) : PassportTokenProperties

@Configuration
@EnableConfigurationProperties(PassportTokenPropertiesImpl::class)
class PassportTokenConfig
