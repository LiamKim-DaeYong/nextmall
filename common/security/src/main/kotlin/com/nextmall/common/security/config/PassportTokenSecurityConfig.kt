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
 * 기본 설정:
 * - CSRF, FormLogin, HttpBasic 비활성화
 * - Stateless 세션 관리
 * - actuator 경로 허용
 * - Passport Token 기반 JWT 인증
 *
 * 커스터마이징:
 * - securityFilterChain Bean을 재정의하여 permitAll 경로 추가 가능
 * - 예: auth-service는 jwks.json 경로 추가
 */
@Configuration
class PassportTokenSecurityConfig {
    @Bean
    @ConditionalOnMissingBean(name = ["passportTokenJwtDecoder"])
    fun passportTokenJwtDecoder(passportTokenProperties: PassportTokenProperties): JwtDecoder =
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
                    jwt.jwtAuthenticationConverter(PassportJwtAuthenticationConverter())
                }
            }.build()

    companion object {
        private val DEFAULT_PERMIT_ALL_PATHS = arrayOf("/actuator/**")
    }
}