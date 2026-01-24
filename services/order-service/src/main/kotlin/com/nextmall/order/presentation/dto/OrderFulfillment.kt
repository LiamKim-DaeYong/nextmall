package com.nextmall.order.presentation.dto

data class OrderFulfillment(
    val expectations: List<Map<String, Any>> = emptyList(),
    val events: List<Map<String, Any>> = emptyList(),
)
