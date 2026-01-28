package com.nextmall.common.kafka.event.order

import com.nextmall.common.kafka.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class OrderCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val eventType: String = "order.created",
    override val occurredAt: Instant = Instant.now(),
    override val source: String = "order-service",
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val userId: Long? = null,
) : DomainEvent
