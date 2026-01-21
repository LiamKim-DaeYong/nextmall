package com.nextmall.bff.client.order

import com.nextmall.bff.client.order.response.CreateOrderClientResponse
import com.nextmall.bff.client.order.response.OrderViewClientResponse
import com.nextmall.common.util.Money
import reactor.core.publisher.Mono

interface OrderServiceClient {
    fun getOrder(orderId: Long): Mono<OrderViewClientResponse>

    fun getOrdersByUserId(userId: Long): Mono<List<OrderViewClientResponse>>

    fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): Mono<CreateOrderClientResponse>

    fun cancelOrder(orderId: Long): Mono<Void>
}
