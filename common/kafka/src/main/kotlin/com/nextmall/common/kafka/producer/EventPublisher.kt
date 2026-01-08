package com.nextmall.common.kafka.producer

import com.nextmall.common.kafka.event.DomainEvent

/**
 * 도메인 이벤트 발행을 위한 추상화 인터페이스.
 * Kafka 외의 다른 메시징 시스템으로 교체 가능하도록 추상화.
 */
interface EventPublisher {
    /**
     * 이벤트를 지정된 토픽에 발행한다.
     * 키는 이벤트의 eventId를 사용한다.
     */
    fun <T : DomainEvent> publish(
        topic: String,
        event: T,
    )

    /**
     * 이벤트를 지정된 토픽에 특정 키로 발행한다.
     * 동일 키의 이벤트는 동일 파티션으로 전송되어 순서가 보장된다.
     */
    fun <T : DomainEvent> publish(
        topic: String,
        key: String,
        event: T,
    )
}
