package com.nextmall.bff.client.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.auth")
data class AuthServiceClientProperties(
    val baseUrl: String,
)
