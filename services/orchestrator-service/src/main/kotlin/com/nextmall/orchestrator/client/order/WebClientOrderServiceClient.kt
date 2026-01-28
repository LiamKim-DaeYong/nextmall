package com.nextmall.orchestrator.client.order

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationFilter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

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
    ): Mono<CreateOrderClientResponse> =
        client
            .post()
            .uri(ORDER_CREATE_URI)
            .bodyValue(request)
            .retrieve()
            .bodyToMono<CreateOrderClientResponse>()

    companion object {
        private const val ORDER_CREATE_URI = "/orders"
    }
}
