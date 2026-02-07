package com.nextmall.order.fixture

import com.nextmall.order.presentation.dto.CreateOrderSnapshotRequest
import com.nextmall.order.presentation.dto.MoneyAmountRequest
import com.nextmall.order.presentation.dto.OrderLineItemRequest
import com.nextmall.order.presentation.dto.OrderTotalsRequest

fun createOrderSnapshotRequest(
    checkoutId: String = "checkout-1",
    currency: String = "KRW",
    lineItems: List<OrderLineItemRequest> = listOf(orderLineItemRequest()),
    totals: OrderTotalsRequest = orderTotalsRequest(),
    permalinkUrl: String? = "https://example.com/orders/1",
) = CreateOrderSnapshotRequest(
    checkoutId = checkoutId,
    lineItems = lineItems,
    totals = totals,
    currency = currency,
    permalinkUrl = permalinkUrl,
)

fun orderLineItemRequest(
    lineItemId: String = "line-1",
    productId: String = "1",
    title: String = "테스트 상품",
    quantity: Int = 2,
    price: MoneyAmountRequest = moneyAmountRequest(),
    imageUrl: String? = "https://example.com/images/1",
) = OrderLineItemRequest(
    lineItemId = lineItemId,
    productId = productId,
    title = title,
    quantity = quantity,
    price = price,
    imageUrl = imageUrl,
)

fun orderTotalsRequest(
    subtotal: MoneyAmountRequest = moneyAmountRequest(amount = 10000),
    tax: MoneyAmountRequest = moneyAmountRequest(amount = 1000),
    shipping: MoneyAmountRequest = moneyAmountRequest(amount = 2500),
    discount: MoneyAmountRequest = moneyAmountRequest(amount = 0),
    total: MoneyAmountRequest = moneyAmountRequest(amount = 13500),
) = OrderTotalsRequest(
    subtotal = subtotal,
    tax = tax,
    shipping = shipping,
    discount = discount,
    total = total,
)

fun moneyAmountRequest(
    amount: Long = 5000,
    currency: String = "KRW",
) = MoneyAmountRequest(
    amount = amount,
    currency = currency,
)
