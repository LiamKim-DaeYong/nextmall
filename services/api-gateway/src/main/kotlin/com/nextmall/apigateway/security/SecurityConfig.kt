package com.nextmall.apigateway.security

import com.nextmall.apigateway.security.filter.JwtPresenceAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtPresenceAuthenticationFilter: JwtPresenceAuthenticationFilter,
    private val properties: GatewaySecurityProperties,
) {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain =
        http
            // Gateway is stateless
            .securityContextRepository(
                NoOpServerSecurityContextRepository.getInstance(),
            )
            // Disable unused defaults
            .anonymous { it.disable() }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            // CORS is handled explicitly at gateway
            .cors { }
            .authorizeExchange {
                properties.permitAllPaths.forEach { path ->
                    it.pathMatchers(path).permitAll()
                }

                it.anyExchange().authenticated()
            }.addFilterAt(
                jwtPresenceAuthenticationFilter,
                SecurityWebFiltersOrder.AUTHENTICATION,
            ).build()
}
