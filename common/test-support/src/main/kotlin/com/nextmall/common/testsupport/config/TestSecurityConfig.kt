package com.nextmall.common.testsupport.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

/**
 * 테스트용 Security 설정.
 *
 * 컨트롤러 슬라이스 테스트에서 인증 없이 테스트할 수 있도록
 * 모든 요청을 허용합니다.
 *
 * 사용법:
 * ```
 * @WebMvcTest(MyController::class)
 * @Import(TestSecurityConfig::class)
 * class MyControllerTest { ... }
 * ```
 */
@TestConfiguration
@EnableWebSecurity
class TestSecurityConfig {
    @Bean
    @Order(1)
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
}
