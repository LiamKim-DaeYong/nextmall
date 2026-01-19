package com.nextmall.bff.client.order

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.order")
data class OrderServiceClientProperties(
    val baseUrl: String,
)
