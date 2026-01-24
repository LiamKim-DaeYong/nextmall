package com.nextmall.orchestrator.client.product

import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse
import reactor.core.publisher.Mono

interface ProductServiceClient {
    fun getProduct(productId: Long): Mono<ProductViewClientResponse>
}
