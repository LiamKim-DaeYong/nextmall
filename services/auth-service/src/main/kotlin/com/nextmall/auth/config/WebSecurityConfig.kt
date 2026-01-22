package com.nextmall.auth.config

import com.nextmall.common.security.config.PassportTokenSecurityConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
@Import(PassportTokenSecurityConfig::class)
class WebSecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
