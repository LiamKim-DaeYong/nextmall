package com.nextmall.common.security.config

import com.nextmall.common.security.token.PassportTokenProperties
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "token.passport")
class PassportTokenPropertiesImpl(
    @field:NotBlank(message = "Passport token secret key must not be blank")
    override val secretKey: String,
) : PassportTokenProperties

@Configuration
@EnableConfigurationProperties(PassportTokenPropertiesImpl::class)
class PassportTokenConfig
