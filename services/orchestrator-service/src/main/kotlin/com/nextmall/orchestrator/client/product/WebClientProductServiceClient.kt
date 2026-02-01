package com.nextmall.orchestrator.client.product

import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse
import com.nextmall.orchestrator.security.PassportTokenPropagationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class WebClientProductServiceClient(
    restClientBuilder: RestClient.Builder,
    properties: ProductServiceClientProperties,
    passportTokenPropagationInterceptor: PassportTokenPropagationInterceptor,
) : ProductServiceClient {
    private val client =
        restClientBuilder
            .baseUrl(properties.baseUrl)
            .requestInterceptor(passportTokenPropagationInterceptor)
            .build()

    override fun getProduct(productId: Long): ProductViewClientResponse {
        val response =
            client
                .get()
                .uri(PRODUCT_GET_URI, productId)
                .retrieve()
                .body(ProductViewClientResponse::class.java)

        return requireNotNull(response)
    }

    companion object {
        private const val PRODUCT_GET_URI = "/products/{id}"
    }
}
