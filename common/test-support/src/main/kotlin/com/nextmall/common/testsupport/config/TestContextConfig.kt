package com.nextmall.common.testsupport.config

import com.nextmall.common.kafka.event.DomainEvent
import com.nextmall.common.kafka.producer.EventPublisher
import com.nextmall.common.security.token.PassportTokenProperties
import com.nextmall.common.testsupport.security.TestPassportTokenIssuer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestContextConfig {
    @Bean
    fun testPassportTokenIssuer(
        passportTokenProperties: PassportTokenProperties,
    ): TestPassportTokenIssuer = TestPassportTokenIssuer(passportTokenProperties)

    @Bean
    @ConditionalOnMissingBean(EventPublisher::class)
    fun noOpEventPublisher(): EventPublisher = NoOpEventPublisher()
}
class NoOpEventPublisher : EventPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun <T : DomainEvent> publish(topic: String, event: T) {
        log.debug("[NoOp] Event published to topic: {}, event: {}", topic, event)
    }

    override fun <T : DomainEvent> publish(topic: String, key: String, event: T) {
        log.debug("[NoOp] Event published to topic: {}, key: {}, event: {}", topic, key, event)
    }
}
