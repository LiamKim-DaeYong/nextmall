package com.nextmall.common.testsupport.config

import com.nextmall.common.security.token.ServiceTokenProperties
import com.nextmall.common.testsupport.security.TestServiceTokenIssuer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 테스트 공통 설정.
 *
 * 테스트에 필요한 공통 빈들을 제공합니다.
 * 컴포넌트 스캔은 각 테스트 어노테이션(@SpringBootTest, @DataJpaTest 등)에서 처리합니다.
 */
@Configuration
class TestContextConfig {
    @Bean
    fun testServiceTokenIssuer(
        serviceTokenProperties: ServiceTokenProperties,
    ): TestServiceTokenIssuer = TestServiceTokenIssuer(serviceTokenProperties)
}
