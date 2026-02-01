package com.nextmall.orchestrator.client.product

import com.nextmall.common.web.mvc.client.RestClientFactory
import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.body

@Component
class ProductServiceClient(
    restClientFactory: RestClientFactory,
    properties: ProductServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) {
    private val client =
        restClientFactory.create(
            baseUrl = properties.baseUrl,
            interceptor = passportTokenPropagationInterceptor,
        )

    fun getProduct(productId: Long): ProductViewClientResponse {
        val response =
            client
                .get()
                .uri(PRODUCT_GET_URI, productId)
                .retrieve()
                .body<ProductViewClientResponse>()

        return checkNotNull(response) {
            "Product service returned null response for getProduct"
        }
    }

    companion object {
        private const val PRODUCT_GET_URI = "/products/{id}"
    }
}
