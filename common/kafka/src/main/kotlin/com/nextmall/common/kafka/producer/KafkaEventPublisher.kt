package com.nextmall.common.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.nextmall.common.kafka.event.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : EventPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun <T : DomainEvent> publish(
        topic: String,
        event: T,
    ) {
        publish(topic, event.eventId, event)
    }

    override fun <T : DomainEvent> publish(
        topic: String,
        key: String,
        event: T,
    ) {
        val payload = objectMapper.writeValueAsString(event)

        kafkaTemplate
            .send(topic, key, payload)
            .whenComplete { result, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish event: topic={}, eventType={}, eventId={}",
                        topic,
                        event.eventType,
                        event.eventId,
                        ex,
                    )
                } else {
                    log.debug(
                        "Event published: topic={}, partition={}, offset={}, eventType={}, eventId={}",
                        topic,
                        result.recordMetadata.partition(),
                        result.recordMetadata.offset(),
                        event.eventType,
                        event.eventId,
                    )
                }
            }
    }
}
