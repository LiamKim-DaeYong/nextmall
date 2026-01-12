package com.nextmall.bff.client.order

import com.nextmall.bff.client.order.request.CreateOrderClientRequest
import com.nextmall.bff.client.order.response.CreateOrderClientResponse
import com.nextmall.bff.client.order.response.OrderViewClientResponse
import com.nextmall.bff.security.ServiceWebClientFactory
import com.nextmall.common.util.Money
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class WebClientOrderServiceClient(
    serviceWebClientFactory: ServiceWebClientFactory,
    properties: OrderServiceClientProperties,
) : OrderServiceClient {
    private val client = serviceWebClientFactory.create(properties.baseUrl, TARGET_SERVICE)

    override suspend fun getOrder(orderId: Long): OrderViewClientResponse =
        client
            .get()
            .uri(ORDER_GET_URI, orderId)
            .retrieve()
            .awaitBody<OrderViewClientResponse>()

    override suspend fun getOrdersByUserId(userId: Long): List<OrderViewClientResponse> =
        client
            .get()
            .uri(ORDER_LIST_BY_USER_URI, userId)
            .retrieve()
            .awaitBody<List<OrderViewClientResponse>>()

    override suspend fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): CreateOrderClientResponse =
        client
            .post()
            .uri(ORDER_CREATE_URI)
            .bodyValue(CreateOrderClientRequest(userId, productId, quantity, totalPrice))
            .retrieve()
            .awaitBody<CreateOrderClientResponse>()

    override suspend fun cancelOrder(orderId: Long) {
        client
            .post()
            .uri(ORDER_CANCEL_URI, orderId)
            .retrieve()
            .toBodilessEntity()
            .awaitSingle()
    }

    companion object {
        private const val TARGET_SERVICE = "order-service"
        private const val ORDER_GET_URI = "/orders/{id}"
        private const val ORDER_LIST_BY_USER_URI = "/orders/users/{userId}"
        private const val ORDER_CREATE_URI = "/orders"
        private const val ORDER_CANCEL_URI = "/orders/{id}/cancel"
    }
}
