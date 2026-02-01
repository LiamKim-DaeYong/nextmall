package com.nextmall.orchestrator.client.order

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationFilter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class WebClientOrderServiceClient(
    webClientFactory: WebClientFactory,
    properties: OrderServiceClientProperties,
) : OrderServiceClient {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    override fun createOrder(
        request: CreateOrderSnapshotClientRequest,
    ): CreateOrderClientResponse {
        val response =
            client
                .post()
                .uri(ORDER_CREATE_URI)
                .bodyValue(request)
                .retrieve()
                .bodyToMono<CreateOrderClientResponse>()
                .block()

        return requireNotNull(response)
    }

    companion object {
        private const val ORDER_CREATE_URI = "/orders"
    }
}
