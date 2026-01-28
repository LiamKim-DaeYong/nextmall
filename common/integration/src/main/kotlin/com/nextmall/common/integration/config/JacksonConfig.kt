package com.nextmall.common.integration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.module.kotlin.KotlinModule

@Configuration
class JacksonConfig {
    @Bean
    fun kotlinModule(): KotlinModule = KotlinModule.Builder().build()
}
