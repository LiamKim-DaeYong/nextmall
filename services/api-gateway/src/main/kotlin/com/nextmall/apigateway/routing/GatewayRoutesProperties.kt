package com.nextmall.apigateway.routing

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway")
data class GatewayRoutesProperties(
    val bff: Service,
) {
    data class Service(
        val baseUrl: String,
    )
}
