package com.nextmall.orchestrator.client.product

import com.nextmall.common.integration.support.WebClientFactory
import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationFilter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class WebClientProductServiceClient(
    webClientFactory: WebClientFactory,
    properties: ProductServiceClientProperties,
) : ProductServiceClient {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    override fun getProduct(productId: Long): Mono<ProductViewClientResponse> =
        client
            .get()
            .uri(PRODUCT_GET_URI, productId)
            .retrieve()
            .bodyToMono<ProductViewClientResponse>()

    companion object {
        private const val PRODUCT_GET_URI = "/products/{id}"
    }
}
