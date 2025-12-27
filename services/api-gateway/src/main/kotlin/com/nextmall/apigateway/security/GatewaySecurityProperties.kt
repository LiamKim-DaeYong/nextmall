package com.nextmall.apigateway.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway.security")
data class GatewaySecurityProperties(
    val permitAllPaths: List<String> = emptyList(),
)
