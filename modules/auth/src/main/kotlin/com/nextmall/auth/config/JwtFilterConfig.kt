package com.nextmall.auth.config

import com.nextmall.auth.infrastructure.security.JwtTokenProvider
import com.nextmall.auth.infrastructure.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtFilterConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
) {
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtTokenProvider, jwtProperties)
}
