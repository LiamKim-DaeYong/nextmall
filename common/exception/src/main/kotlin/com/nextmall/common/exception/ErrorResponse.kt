package com.nextmall.common.exception

import java.time.Instant

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val path: String? = null,
    val traceId: String? = null,
    val details: Map<String, Any?>? = null,
)
