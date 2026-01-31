package com.nextmall.orchestrator.application.order.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InsufficientStockException(
    productId: Long,
    quantity: Int,
) : ResponseStatusException(
        HttpStatus.CONFLICT,
        "Insufficient stock for productId=$productId, quantity=$quantity",
    )
