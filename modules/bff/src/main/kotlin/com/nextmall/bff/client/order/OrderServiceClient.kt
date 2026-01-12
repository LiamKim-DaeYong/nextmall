package com.nextmall.bff.client.order

import com.nextmall.bff.client.order.response.CreateOrderClientResponse
import com.nextmall.bff.client.order.response.OrderViewClientResponse
import com.nextmall.common.util.Money

interface OrderServiceClient {
    suspend fun getOrder(orderId: Long): OrderViewClientResponse

    suspend fun getOrdersByUserId(userId: Long): List<OrderViewClientResponse>

    suspend fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): CreateOrderClientResponse

    suspend fun cancelOrder(orderId: Long)
}
