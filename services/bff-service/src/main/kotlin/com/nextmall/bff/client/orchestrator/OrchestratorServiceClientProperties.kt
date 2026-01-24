package com.nextmall.bff.client.orchestrator

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "client.orchestrator")
data class OrchestratorServiceClientProperties(
    val baseUrl: String,
)
