package com.nextmall.checkout.domain.model

import java.time.Instant

data class FulfillmentExpectation(
    val type: String,
    val details: String? = null,
)

data class FulfillmentEvent(
    val type: String,
    val occurredAt: Instant? = null,
)

data class Fulfillment(
    val expectations: List<FulfillmentExpectation>,
    val events: List<FulfillmentEvent>,
)
