package com.nextmall.apigateway.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

/**
 * Gateway 인증 정책:
 *
 * - Gateway는 Access Token을 검증한다 (JWKS 기반)
 * - 만료, 서명 검증을 수행한다
 * - 검증된 토큰은 하위 서비스로 전달한다
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val properties: GatewaySecurityProperties,
) {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain =
        http
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
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
            }.oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }.build()
}
