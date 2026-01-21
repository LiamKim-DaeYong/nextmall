package com.nextmall.bff.client.product

import com.nextmall.bff.client.product.response.ProductViewClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.core.ParameterizedTypeReference
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

    override fun getAllProducts(): Mono<List<ProductViewClientResponse>> =
        client
            .get()
            .uri(PRODUCT_LIST_URI)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<ProductViewClientResponse>>() {})

    companion object {
        private const val PRODUCT_GET_URI = "/products/{id}"
        private const val PRODUCT_LIST_URI = "/products"
    }
}
