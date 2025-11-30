package com.nextmall.auth.config

import com.nextmall.auth.domain.jwt.TokenProvider
import com.nextmall.auth.infrastructure.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtFilterConfig(
    private val tokenProvider: TokenProvider,
    private val jwtProperties: JwtProperties,
) {
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(tokenProvider, jwtProperties)
}
