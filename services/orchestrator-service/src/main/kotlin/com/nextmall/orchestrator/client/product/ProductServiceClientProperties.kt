package com.nextmall.orchestrator.client.product

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "client.product")
data class ProductServiceClientProperties(
    @field:NotBlank
    val baseUrl: String,
)
