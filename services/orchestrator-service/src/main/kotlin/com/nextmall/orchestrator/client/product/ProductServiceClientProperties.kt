package com.nextmall.orchestrator.client.product

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.product")
data class ProductServiceClientProperties(
    val baseUrl: String,
)
