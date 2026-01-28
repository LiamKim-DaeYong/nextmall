package com.nextmall.orchestrator.client.order

import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import reactor.core.publisher.Mono

interface OrderServiceClient {
    fun createOrder(
        request: CreateOrderSnapshotClientRequest,
    ): Mono<CreateOrderClientResponse>
}
