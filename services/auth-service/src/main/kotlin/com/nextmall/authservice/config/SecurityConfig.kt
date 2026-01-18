package com.nextmall.authservice.config

import com.nextmall.common.security.internal.ServiceBearerTokenResolver
import com.nextmall.common.security.jwt.JwtDecoderFactory
import com.nextmall.common.security.spring.ServiceJwtAuthenticationConverter
import com.nextmall.common.security.token.ServiceTokenProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val serviceTokenProperties: ServiceTokenProperties,
) {
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
                    .requestMatchers("/actuator/**", "/.well-known/jwks.json")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenResolver(ServiceBearerTokenResolver())
                oauth2.jwt { jwt ->
                    jwt.decoder(serviceTokenJwtDecoder())
                    jwt.jwtAuthenticationConverter(serviceJwtAuthenticationConverter())
                }
            }.build()

    @Bean
    fun serviceTokenJwtDecoder(): JwtDecoder =
        JwtDecoderFactory.create(serviceTokenProperties.secretKey)

    @Bean
    fun serviceJwtAuthenticationConverter(): ServiceJwtAuthenticationConverter =
        ServiceJwtAuthenticationConverter()
}
