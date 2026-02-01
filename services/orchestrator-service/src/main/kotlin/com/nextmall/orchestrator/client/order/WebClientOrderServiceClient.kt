package com.nextmall.orchestrator.client.order

import com.nextmall.orchestrator.client.order.request.CreateOrderSnapshotClientRequest
import com.nextmall.orchestrator.client.order.response.CreateOrderClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class WebClientOrderServiceClient(
    restClientBuilder: RestClient.Builder,
    properties: OrderServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) : OrderServiceClient {
    private val client =
        restClientBuilder
            .baseUrl(properties.baseUrl)
            .requestInterceptor(passportTokenPropagationInterceptor)
            .build()

    override fun createOrder(
        request: CreateOrderSnapshotClientRequest,
    ): CreateOrderClientResponse {
        val response =
            client
                .post()
                .uri(ORDER_CREATE_URI)
                .body(request)
                .retrieve()
                .body(CreateOrderClientResponse::class.java)

        return requireNotNull(response)
    }

    companion object {
        private const val ORDER_CREATE_URI = "/orders"
    }
}
