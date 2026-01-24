package com.nextmall.bff.application.order.command

import com.nextmall.bff.client.orchestrator.OrchestratorServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateOrderFacade(
    private val orchestratorServiceClient: OrchestratorServiceClient,
) {
    /**
     * 주문 생성 오케스트레이션을 호출한다.
     */
    fun createOrder(command: CreateOrderCommand): Mono<CreateOrderResult> =
        orchestratorServiceClient
            .createOrder(
                userId = command.userId,
                productId = command.productId,
                quantity = command.quantity,
            ).map { response -> CreateOrderResult(orderId = response.orderId) }
}
