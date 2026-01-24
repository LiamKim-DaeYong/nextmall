package com.nextmall.orchestrator.config

import com.nextmall.common.security.jwt.SecretKeyDecoder
import com.nextmall.common.security.spring.PassportJwtAuthenticationConverter
import com.nextmall.common.security.token.PassportTokenProperties
import com.nextmall.orchestrator.security.PassportBearerTokenAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val passportTokenProperties: PassportTokenProperties,
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/actuator/**", "/.well-known/jwks.json")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenConverter(PassportBearerTokenAuthenticationConverter())
                oauth2.jwt { jwt ->
                    jwt.jwtDecoder(jwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }.build()

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder
            .withSecretKey(
                SecretKeyDecoder.decode(passportTokenProperties.secretKey),
            ).build()

    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverterAdapter =
        ReactiveJwtAuthenticationConverterAdapter(PassportJwtAuthenticationConverter())
}
