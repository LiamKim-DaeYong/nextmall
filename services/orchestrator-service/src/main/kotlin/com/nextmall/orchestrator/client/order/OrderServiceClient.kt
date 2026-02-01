package com.nextmall.orchestrator.client.order

import com.nextmall.common.web.mvc.client.RestClientFactory
import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.body

@Component
class OrderServiceClient(
    restClientFactory: RestClientFactory,
    properties: OrderServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) {
    private val client =
        restClientFactory.create(
            baseUrl = properties.baseUrl,
            interceptor = passportTokenPropagationInterceptor,
        )

    fun createOrder(
        request: CreateOrderSnapshotClientRequest,
    ): CreateOrderClientResponse {
        val response =
            client
                .post()
                .uri(ORDER_CREATE_URI)
                .body(request)
                .retrieve()
                .body<CreateOrderClientResponse>()

        return checkNotNull(response) {
            "Order service returned null response for createOrder"
        }
    }

    companion object {
        private const val ORDER_CREATE_URI = "/orders"
    }
}
