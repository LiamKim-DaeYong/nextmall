package com.nextmall.bff.application.order.query

import com.nextmall.bff.client.order.OrderServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetOrderFacade(
    private val orderServiceClient: OrderServiceClient,
) {
    /**
     * 주문 단건을 조회해 BFF 응답 모델로 변환한다.
     */
    fun getOrder(orderId: Long): Mono<OrderViewResult> =
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

    /**
     * 사용자 기준 주문 목록을 조회해 BFF 응답 모델로 변환한다.
     */
    fun getOrdersByUserId(userId: Long): Mono<List<OrderViewResult>> =
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
