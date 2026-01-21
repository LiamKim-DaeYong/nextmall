package com.nextmall.bff.client.order

import com.nextmall.bff.client.order.request.CreateOrderClientRequest
import com.nextmall.bff.client.order.response.CreateOrderClientResponse
import com.nextmall.bff.client.order.response.OrderViewClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.common.util.Money
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

    override fun getOrder(orderId: Long): Mono<OrderViewClientResponse> =
        client
            .get()
            .uri(ORDER_GET_URI, orderId)
            .retrieve()
            .bodyToMono<OrderViewClientResponse>()

    override fun getOrdersByUserId(userId: Long): Mono<List<OrderViewClientResponse>> =
        client
            .get()
            .uri(ORDER_LIST_BY_USER_URI, userId)
            .retrieve()
            .bodyToMono(
                object : org.springframework.core.ParameterizedTypeReference<List<OrderViewClientResponse>>() {},
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

    override fun cancelOrder(orderId: Long): Mono<Void> =
        client
            .post()
            .uri(ORDER_CANCEL_URI, orderId)
            .retrieve()
            .bodyToMono<Void>()

    companion object {
        private const val ORDER_GET_URI = "/orders/{id}"
        private const val ORDER_LIST_BY_USER_URI = "/orders/users/{userId}"
        private const val ORDER_CREATE_URI = "/orders"
        private const val ORDER_CANCEL_URI = "/orders/{id}/cancel"
    }
}
