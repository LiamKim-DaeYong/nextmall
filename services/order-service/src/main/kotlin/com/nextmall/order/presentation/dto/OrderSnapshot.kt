package com.nextmall.order.presentation.dto

data class OrderSnapshot(
    val orderId: Long,
    val checkoutId: String,
    val permalinkUrl: String? = null,
    val lineItems: List<OrderLineItem>,
    val fulfillment: OrderFulfillment = OrderFulfillment(),
    val adjustments: List<Map<String, Any>> = emptyList(),
    val totals: OrderTotals,
)
