package com.nextmall.orchestrator.application.order.command

import com.nextmall.orchestrator.client.order.OrderServiceClient
import com.nextmall.orchestrator.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateOrderFacade(
    private val productServiceClient: ProductServiceClient,
    private val orderServiceClient: OrderServiceClient,
) {
    /**
     * 상품 조회와 주문 생성을 오케스트레이션한다.
     */
    fun createOrder(command: CreateOrderCommand): Mono<CreateOrderResult> {
        require(command.quantity > 0) { "Quantity must be positive" }

        return productServiceClient
            .getProduct(command.productId)
            .flatMap { product ->
                val totalPrice = product.price * command.quantity
                orderServiceClient.createOrder(
                    userId = command.userId,
                    productId = command.productId,
                    quantity = command.quantity,
                    totalPrice = totalPrice,
                )
            }.map { response -> CreateOrderResult(orderId = response.orderId) }
    }
}
