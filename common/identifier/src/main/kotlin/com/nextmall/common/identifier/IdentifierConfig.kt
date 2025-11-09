package com.nextmall.common.identifier

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(IdentifierProperties::class)
class IdentifierConfig(
    private val properties: IdentifierProperties,
) {
    @Bean
    fun idGenerator(): IdGenerator = SnowflakeIdGenerator(nodeId = properties.nodeId)
}
