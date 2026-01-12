package com.nextmall.bff.client.product

import com.nextmall.bff.client.product.response.ProductViewClientResponse
import com.nextmall.bff.security.ServiceWebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class WebClientProductServiceClient(
    serviceWebClientFactory: ServiceWebClientFactory,
    properties: ProductServiceClientProperties,
) : ProductServiceClient {
    private val client = serviceWebClientFactory.create(properties.baseUrl, TARGET_SERVICE)

    override suspend fun getProduct(productId: Long): ProductViewClientResponse =
        client
            .get()
            .uri(PRODUCT_GET_URI, productId)
            .retrieve()
            .awaitBody<ProductViewClientResponse>()

    override suspend fun getAllProducts(): List<ProductViewClientResponse> =
        client
            .get()
            .uri(PRODUCT_LIST_URI)
            .retrieve()
            .awaitBody<List<ProductViewClientResponse>>()

    companion object {
        private const val TARGET_SERVICE = "product-service"
        private const val PRODUCT_GET_URI = "/products/{id}"
        private const val PRODUCT_LIST_URI = "/products"
    }
}
