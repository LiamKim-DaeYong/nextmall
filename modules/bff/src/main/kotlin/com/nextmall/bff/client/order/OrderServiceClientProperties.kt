package com.nextmall.bff.client.order

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service.order")
data class OrderServiceClientProperties(
    val baseUrl: String,
)
