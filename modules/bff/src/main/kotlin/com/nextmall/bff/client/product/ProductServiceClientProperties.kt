package com.nextmall.bff.client.product

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service.product")
data class ProductServiceClientProperties(
    val baseUrl: String,
)
