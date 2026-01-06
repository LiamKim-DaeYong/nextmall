package com.nextmall.common.testsupport.config

import com.nextmall.common.security.token.ServiceTokenProperties
import com.nextmall.common.testsupport.security.TestServiceTokenIssuer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["com.nextmall"])
class TestContextConfig {
    @Bean
    fun testServiceTokenIssuer(
        serviceTokenProperties: ServiceTokenProperties,
    ): TestServiceTokenIssuer = TestServiceTokenIssuer(serviceTokenProperties)
}
