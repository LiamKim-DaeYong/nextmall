package com.nextmall.orchestrator.client.order.request

data class CreateOrderSnapshotClientRequest(
    val checkoutId: String,
    val lineItems: List<OrderLineItemClientRequest>,
    val totals: OrderTotalsClientRequest,
    val currency: String,
    val permalinkUrl: String? = null,
)

data class OrderLineItemClientRequest(
    val id: String,
    val productId: String,
    val title: String,
    val quantity: Int,
    val price: MoneyAmountClientRequest,
    val imageUrl: String? = null,
)

data class OrderTotalsClientRequest(
    val subtotal: MoneyAmountClientRequest,
    val tax: MoneyAmountClientRequest,
    val shipping: MoneyAmountClientRequest,
    val discount: MoneyAmountClientRequest,
    val total: MoneyAmountClientRequest,
)

data class MoneyAmountClientRequest(
    val amount: Long,
    val currency: String,
)
