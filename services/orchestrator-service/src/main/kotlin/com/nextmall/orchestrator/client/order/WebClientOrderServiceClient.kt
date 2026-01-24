package com.nextmall.orchestrator.client.order

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.common.util.Money
import com.nextmall.orchestrator.client.order.request.CreateOrderClientRequest
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
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): Mono<CreateOrderClientResponse> =
        client
            .post()
            .uri(ORDER_CREATE_URI)
            .bodyValue(CreateOrderClientRequest(userId, productId, quantity, totalPrice))
            .retrieve()
            .bodyToMono<CreateOrderClientResponse>()

    companion object {
        private const val ORDER_CREATE_URI = "/orders"
    }
}
