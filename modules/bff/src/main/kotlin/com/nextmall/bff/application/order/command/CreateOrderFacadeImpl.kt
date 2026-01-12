package com.nextmall.bff.application.order.command

import com.nextmall.bff.client.order.OrderServiceClient
import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component

@Component
class CreateOrderFacadeImpl(
    private val productServiceClient: ProductServiceClient,
    private val orderServiceClient: OrderServiceClient,
) : CreateOrderFacade {
    override suspend fun createOrder(command: CreateOrderCommand): CreateOrderResult {
        // 1. Product 가격 조회
        val product = productServiceClient.getProduct(command.productId)

        // 2. Order 생성 (실제 가격 전달)
        val response = orderServiceClient.createOrder(
            userId = command.userId,
            productId = command.productId,
            quantity = command.quantity,
        )

        return CreateOrderResult(orderId = response.orderId)
    }
}
