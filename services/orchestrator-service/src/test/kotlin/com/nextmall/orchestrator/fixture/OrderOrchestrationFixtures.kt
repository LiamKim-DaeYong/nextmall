package com.nextmall.orchestrator.fixture

import com.nextmall.common.util.Money
import com.nextmall.orchestrator.application.order.command.CreateOrderCommand
import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse
import java.math.BigDecimal

fun createOrderCommand(
    userId: Long = 1L,
    productId: Long = 10L,
    quantity: Int = 2,
) = CreateOrderCommand(
    userId = userId,
    productId = productId,
    quantity = quantity,
)

fun productViewClientResponse(
    id: Long = 10L,
    name: String = "테스트 상품",
    price: Money = Money.of(BigDecimal("12.34")),
    currency: String? = "USD",
    stock: Int = 10,
    sellerId: Long = 99L,
    category: String? = "test",
) = ProductViewClientResponse(
    id = id,
    name = name,
    price = price,
    currency = currency,
    stock = stock,
    sellerId = sellerId,
    category = category,
)
