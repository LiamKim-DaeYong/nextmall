package com.nextmall.bff.client.product

import com.nextmall.bff.client.product.request.CreateProductClientRequest
import com.nextmall.bff.client.product.request.UpdateProductClientRequest
import com.nextmall.bff.client.product.response.CreateProductClientResponse
import com.nextmall.bff.client.product.response.ProductViewClientResponse
import com.nextmall.bff.client.product.response.SellerProductViewClientResponse
import com.nextmall.bff.security.PassportTokenPropagationFilter
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Component
class ProductServiceClient(
    webClientFactory: WebClientFactory,
    properties: ProductServiceClientProperties,
) {
    private val client =
        webClientFactory.create(
            baseUrl = properties.baseUrl,
            filters = arrayOf(PassportTokenPropagationFilter()),
        )

    fun getProduct(productId: Long): Mono<ProductViewClientResponse> =
        client
            .get()
            .uri(PRODUCT_GET_URI, productId)
            .retrieve()
            .bodyToMono<ProductViewClientResponse>()

    fun getAllProducts(): Mono<List<ProductViewClientResponse>> =
        client
            .get()
            .uri(PRODUCT_LIST_URI)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<ProductViewClientResponse>>() {})

    fun createProduct(
        name: String,
        price: BigDecimal,
        stock: Int,
        category: String?,
    ): Mono<CreateProductClientResponse> =
        client
            .post()
            .uri(PRODUCT_CREATE_URI)
            .bodyValue(CreateProductClientRequest(name, price, stock, category))
            .retrieve()
            .bodyToMono<CreateProductClientResponse>()

    fun updateProduct(
        productId: Long,
        request: UpdateProductClientRequest,
    ): Mono<Void> =
        client
            .put()
            .uri(PRODUCT_UPDATE_URI, productId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)

    fun deleteProduct(productId: Long): Mono<Void> =
        client
            .delete()
            .uri(PRODUCT_DELETE_URI, productId)
            .retrieve()
            .bodyToMono(Void::class.java)

    fun getMyProduct(productId: Long): Mono<SellerProductViewClientResponse> =
        client
            .get()
            .uri(SELLER_PRODUCT_GET_URI, productId)
            .retrieve()
            .bodyToMono<SellerProductViewClientResponse>()

    fun getMyProducts(): Mono<List<SellerProductViewClientResponse>> =
        client
            .get()
            .uri(SELLER_PRODUCT_LIST_URI)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<SellerProductViewClientResponse>>() {})

    fun suspendProduct(productId: Long): Mono<Void> =
        client
            .post()
            .uri(PRODUCT_SUSPEND_URI, productId)
            .retrieve()
            .bodyToMono(Void::class.java)

    companion object {
        private const val PRODUCT_GET_URI = "/products/{id}"
        private const val PRODUCT_LIST_URI = "/products"
        private const val PRODUCT_CREATE_URI = "/products"
        private const val PRODUCT_UPDATE_URI = "/products/{id}"
        private const val PRODUCT_DELETE_URI = "/products/{id}"
        private const val PRODUCT_SUSPEND_URI = "/products/{id}/suspend"
        private const val SELLER_PRODUCT_GET_URI = "/sellers/me/products/{id}"
        private const val SELLER_PRODUCT_LIST_URI = "/sellers/me/products"
    }
}
