package com.nextmall.common.kafka.event

import java.time.Instant

/**
 * 모든 도메인 이벤트의 기본 인터페이스.
 * Kafka를 통해 발행되는 이벤트는 이 인터페이스를 구현해야 한다.
 */
interface DomainEvent {
    /** 이벤트 고유 식별자 */
    val eventId: String

    /** 이벤트 타입 (예: "user.created", "permission.changed") */
    val eventType: String

    /** 이벤트 발생 시각 */
    val occurredAt: Instant

    /** 이벤트 소스 서비스 */
    val source: String
}
