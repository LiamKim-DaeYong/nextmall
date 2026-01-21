package com.nextmall.common.security.config

import com.nextmall.common.security.internal.PassportBearerTokenResolver
import com.nextmall.common.security.jwt.JwtDecoderFactory
import com.nextmall.common.security.spring.PassportJwtAuthenticationConverter
import com.nextmall.common.security.token.PassportTokenProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain

/**
 * Passport Token 기반 내부 서비스 인증을 위한 공통 Security 설정
 *
 * Gateway 뒤의 모든 내부 서비스가 동일하게 사용하는 설정:
 * - 모든 요청에 Passport Token 필수 (Gateway가 발급)
 * - permitAll: /actuator, /.well-known/jwks.json
 * - Passport Token 기반 JWT 인증
 */
@Configuration
class PassportTokenSecurityConfig(
    private val passportTokenProperties: PassportTokenProperties,
) {
    @Bean
    @ConditionalOnMissingBean(name = ["passportTokenJwtDecoder"])
    fun passportTokenJwtDecoder(): JwtDecoder =
        JwtDecoderFactory.create(passportTokenProperties.secretKey)

    @Bean
    @ConditionalOnMissingBean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authorizeHttpRequests {
                it
                    .requestMatchers(*DEFAULT_PERMIT_ALL_PATHS)
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenResolver(PassportBearerTokenResolver())
                oauth2.jwt { jwt ->
                    jwt.decoder(passportTokenJwtDecoder())
                    jwt.jwtAuthenticationConverter(PassportJwtAuthenticationConverter())
                }
            }.build()

    companion object {
        private val DEFAULT_PERMIT_ALL_PATHS =
            arrayOf(
                "/actuator/**",
                "/.well-known/jwks.json",
            )
    }
}
