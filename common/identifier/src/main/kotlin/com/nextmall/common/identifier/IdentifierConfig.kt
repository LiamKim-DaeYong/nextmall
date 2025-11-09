package com.nextmall.common.identifier

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdentifierConfig {
    @Bean
    fun idGenerator(): IdGenerator = SnowflakeIdGenerator(nodeId = 1L)
}
