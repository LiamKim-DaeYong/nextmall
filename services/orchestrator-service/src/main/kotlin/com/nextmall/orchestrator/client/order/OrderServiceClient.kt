package com.nextmall.orchestrator.client.order

import com.nextmall.common.util.Money
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import reactor.core.publisher.Mono

interface OrderServiceClient {
    fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): Mono<CreateOrderClientResponse>
}
