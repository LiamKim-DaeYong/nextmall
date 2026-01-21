package com.nextmall.bff.client.product

import com.nextmall.bff.client.product.response.ProductViewClientResponse
import reactor.core.publisher.Mono

interface ProductServiceClient {
    fun getProduct(productId: Long): Mono<ProductViewClientResponse>

    fun getAllProducts(): Mono<List<ProductViewClientResponse>>
}
