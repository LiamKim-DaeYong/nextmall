package com.nextmall.bffservice.config

import com.nextmall.common.security.token.ServiceTokenProperties
import com.nextmall.common.security.token.UserTokenProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "token.user")
class UserTokenPropertiesImpl(
    override val secretKey: String,
) : UserTokenProperties

@ConfigurationProperties(prefix = "token.service")
class ServiceTokenPropertiesImpl(
    override val secretKey: String,
) : ServiceTokenProperties

@Configuration
@EnableConfigurationProperties(UserTokenPropertiesImpl::class, ServiceTokenPropertiesImpl::class)
class TokenConfig
