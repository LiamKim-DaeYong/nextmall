package com.nextmall.orchestrator.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import com.nextmall.common.web.mvc.security.config.PassportTokenSecurityConfig

@Configuration
@EnableWebSecurity
@Import(PassportTokenSecurityConfig::class)
class SecurityConfig
