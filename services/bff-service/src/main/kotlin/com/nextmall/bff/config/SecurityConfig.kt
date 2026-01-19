package com.nextmall.bff.config

import com.nextmall.common.security.internal.PassportTokenConstants
import com.nextmall.common.security.jwt.JwtDecoderFactory
import com.nextmall.common.security.principal.JwtToPrincipalConverter
import com.nextmall.common.security.spring.SpringJwtAuthenticationConverter
import com.nextmall.common.security.token.PassportTokenProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val passportTokenProperties: PassportTokenProperties,
) {
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
                    .requestMatchers(
                        "/auth/**",
                        "/sign-up/**",
                        "/token/**",
                        "/actuator/**",
                        "/error",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenResolver(passportTokenResolver())
                oauth2.jwt { jwt ->
                    jwt.decoder(passportTokenJwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }.build()

    @Bean
    fun passportTokenResolver() =
        DefaultBearerTokenResolver().apply {
            setBearerTokenHeaderName(PassportTokenConstants.HEADER_NAME)
        }

    @Bean
    fun passportTokenJwtDecoder(): JwtDecoder =
        JwtDecoderFactory.create(passportTokenProperties.secretKey)

    @Bean
    fun jwtAuthenticationConverter(): SpringJwtAuthenticationConverter =
        SpringJwtAuthenticationConverter(
            JwtToPrincipalConverter(),
        )
}
