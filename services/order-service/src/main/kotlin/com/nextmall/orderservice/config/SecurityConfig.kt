package com.nextmall.orderservice.config

import com.nextmall.common.security.config.PassportTokenSecurityConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Configuration
@EnableWebSecurity
@Import(PassportTokenSecurityConfig::class)
class SecurityConfig
