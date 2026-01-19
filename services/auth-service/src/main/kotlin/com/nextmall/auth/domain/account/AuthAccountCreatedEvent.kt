package com.nextmall.auth.domain.account

import com.nextmall.common.kafka.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class AuthAccountCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val eventType: String = "auth.account.created",
    override val occurredAt: Instant = Instant.now(),
    override val source: String = "auth-service",
    val accountId: Long,
    val userId: Long,
    val provider: String,
) : DomainEvent
