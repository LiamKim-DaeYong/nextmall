package com.nextmall.orchestrator.client.user

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.user")
data class UserServiceClientProperties(
    val baseUrl: String,
)
