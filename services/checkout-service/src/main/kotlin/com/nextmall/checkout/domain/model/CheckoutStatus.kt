package com.nextmall.checkout.domain.model

enum class CheckoutStatus {
    INCOMPLETE,
    READY_FOR_COMPLETE,
    COMPLETE_IN_PROGRESS,
    COMPLETED,
    REQUIRES_ESCALATION,
    CANCELED,
}
