package com.nextmall.apigateway.security.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway.security")
data class GatewaySecurityProperties(
    val permitAllPaths: List<String> = emptyList(),
)
