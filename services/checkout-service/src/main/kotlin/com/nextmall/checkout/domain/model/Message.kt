package com.nextmall.checkout.domain.model

enum class MessageSeverity {
    INFO,
    WARNING,
    ERROR,
    ESCALATION,
}

data class Message(
    val code: String,
    val message: String?,
    val severity: MessageSeverity,
)
