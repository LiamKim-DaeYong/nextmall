package com.nextmall.auth.config

import com.nextmall.common.security.config.PassportTokenSecurityConfig
import com.nextmall.common.security.internal.PassportBearerTokenResolver
import com.nextmall.common.security.spring.PassportJwtAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@Import(PassportTokenSecurityConfig::class)
class WebSecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authorizeHttpRequests {
                it
                    .requestMatchers(*PERMIT_ALL_PATHS)
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenResolver(PassportBearerTokenResolver())
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(PassportJwtAuthenticationConverter())
                }
            }.build()

    companion object {
        private val PERMIT_ALL_PATHS = arrayOf("/actuator/**", "/.well-known/jwks.json")
    }
}
