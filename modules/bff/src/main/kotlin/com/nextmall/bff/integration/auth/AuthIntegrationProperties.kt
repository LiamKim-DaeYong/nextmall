package com.nextmall.bff.integration.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "integration.auth")
data class AuthIntegrationProperties(
    val baseUrl: String,
)
