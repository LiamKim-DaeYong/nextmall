package com.nextmall.authservice.config

import com.nextmall.auth.config.UserTokenIssuerProperties
import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.service")
class ServiceTokenPropertiesImpl(
    override val secretKey: String,
) : ServiceTokenProperties

@Configuration
@EnableConfigurationProperties(UserTokenIssuerProperties::class, ServiceTokenPropertiesImpl::class)
class TokenConfig
