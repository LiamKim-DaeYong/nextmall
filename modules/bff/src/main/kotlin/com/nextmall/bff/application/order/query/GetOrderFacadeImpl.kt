package com.nextmall.bff.application.order.query

import com.nextmall.bff.client.order.OrderServiceClient
import org.springframework.stereotype.Component

@Component
class GetOrderFacadeImpl(
    private val orderServiceClient: OrderServiceClient,
) : GetOrderFacade {
    override suspend fun getOrder(orderId: Long): OrderViewResult {
        val order = orderServiceClient.getOrder(orderId)
        return OrderViewResult(
            id = order.id,
            userId = order.userId,
            productId = order.productId,
            quantity = order.quantity,
            totalPrice = order.totalPrice,
            status = order.status,
        )
    }

    override suspend fun getOrdersByUserId(userId: Long): List<OrderViewResult> =
        orderServiceClient.getOrdersByUserId(userId).map {
            OrderViewResult(
                id = it.id,
                userId = it.userId,
                productId = it.productId,
                quantity = it.quantity,
                totalPrice = it.totalPrice,
                status = it.status,
            )
        }
}
