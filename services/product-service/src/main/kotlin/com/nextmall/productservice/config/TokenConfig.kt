package com.nextmall.productservice.config

import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.service")
class ServiceTokenPropertiesImpl(
    override val secretKey: String,
) : ServiceTokenProperties

@Configuration
@EnableConfigurationProperties(ServiceTokenPropertiesImpl::class)
class TokenConfig
