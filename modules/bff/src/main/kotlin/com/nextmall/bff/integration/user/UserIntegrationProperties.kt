package com.nextmall.bff.integration.user

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "integration.user")
data class UserIntegrationProperties(
    val baseUrl: String,
)
