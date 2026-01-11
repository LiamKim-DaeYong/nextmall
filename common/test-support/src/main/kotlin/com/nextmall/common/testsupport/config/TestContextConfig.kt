package com.nextmall.common.testsupport.config

import com.nextmall.common.kafka.event.DomainEvent
import com.nextmall.common.kafka.producer.EventPublisher
import com.nextmall.common.security.token.ServiceTokenProperties
import com.nextmall.common.testsupport.security.TestServiceTokenIssuer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
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

    @Bean
    @ConditionalOnMissingBean(EventPublisher::class)
    fun noOpEventPublisher(): EventPublisher = NoOpEventPublisher()
}

/**
 * 테스트용 NoOp EventPublisher.
 * Kafka 없이 테스트를 실행할 때 사용.
 */
class NoOpEventPublisher : EventPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun <T : DomainEvent> publish(topic: String, event: T) {
        log.debug("[NoOp] Event published to topic: {}, event: {}", topic, event)
    }

    override fun <T : DomainEvent> publish(topic: String, key: String, event: T) {
        log.debug("[NoOp] Event published to topic: {}, key: {}, event: {}", topic, key, event)
    }
}
