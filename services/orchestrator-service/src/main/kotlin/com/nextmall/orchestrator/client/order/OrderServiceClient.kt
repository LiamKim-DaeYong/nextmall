package com.nextmall.orchestrator.client.order

import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest

interface OrderServiceClient {
    fun createOrder(
        request: CreateOrderSnapshotClientRequest,
    ): CreateOrderClientResponse
}
