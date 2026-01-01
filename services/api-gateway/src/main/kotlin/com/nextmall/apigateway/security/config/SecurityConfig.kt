package com.nextmall.apigateway.security.config

import com.nextmall.apigateway.security.filter.JwtPresenceAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * Gateway 인증 정책:
 *
 * - Gateway는 인증의 "입구" 역할만 수행한다.
 * - JWT의 존재 여부와 기본 형식(Bearer 토큰)만 확인한다.
 * - 토큰의 검증, 디코딩, 사용자 식별은 하위 서비스의 책임이다.
 */
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
