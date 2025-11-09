package com.nextmall.common.identifier

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nextmall.identifier")
data class IdentifierProperties(
    val nodeId: Long = 1L,
)
