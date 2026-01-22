package com.nextmall.bff.application.order.query

import com.nextmall.bff.client.order.OrderServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetOrderFacadeImpl(
    private val orderServiceClient: OrderServiceClient,
) : GetOrderFacade {
    override fun getOrder(orderId: Long): Mono<OrderViewResult> =
        orderServiceClient
            .getOrder(orderId)
            .map { order ->
                OrderViewResult(
                    id = order.id,
                    userId = order.userId,
                    productId = order.productId,
                    quantity = order.quantity,
                    totalPrice = order.totalPrice,
                    status = order.status,
                )
            }

    override fun getOrdersByUserId(userId: Long): Mono<List<OrderViewResult>> =
        orderServiceClient
            .getOrdersByUserId(userId)
            .map { orders ->
                orders.map {
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
}
