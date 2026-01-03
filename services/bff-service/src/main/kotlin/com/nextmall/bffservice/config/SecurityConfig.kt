package com.nextmall.bffservice.config

import com.nextmall.common.security.jwt.JwtDecoderFactory
import com.nextmall.common.security.principal.JwtToPrincipalConverter
import com.nextmall.common.security.spring.SpringJwtAuthenticationConverter
import com.nextmall.common.security.token.UserTokenProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userTokenProperties: UserTokenProperties,
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
                oauth2.jwt { jwt ->
                    jwt.decoder(userTokenJwtDecoder())
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }.build()

    @Bean
    fun userTokenJwtDecoder(): JwtDecoder =
        JwtDecoderFactory.create(userTokenProperties.secretKey)

    @Bean
    fun jwtAuthenticationConverter(): SpringJwtAuthenticationConverter =
        SpringJwtAuthenticationConverter(
            JwtToPrincipalConverter(),
        )
}
