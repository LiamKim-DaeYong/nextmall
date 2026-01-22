package com.nextmall.bff.application.order.command

import com.nextmall.bff.client.order.OrderServiceClient
import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateOrderFacadeImpl(
    private val productServiceClient: ProductServiceClient,
    private val orderServiceClient: OrderServiceClient,
) : CreateOrderFacade {
    override fun createOrder(command: CreateOrderCommand): Mono<CreateOrderResult> {
        // 0. 수량 유효성 검증
        require(command.quantity > 0) { "Quantity must be positive" }

        // 1. Product 가격 조회
        return productServiceClient
            .getProduct(command.productId)
            .flatMap { product ->
                // 2. 총 가격 계산
                val totalPrice = product.price * command.quantity

                // 3. Order 생성 (실제 가격 전달)
                orderServiceClient
                    .createOrder(
                        userId = command.userId,
                        productId = command.productId,
                        quantity = command.quantity,
                        totalPrice = totalPrice,
                    )
            }.map { response -> CreateOrderResult(orderId = response.orderId) }
    }
}
