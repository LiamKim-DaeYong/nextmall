package com.nextmall.bff.application.product.query

import reactor.core.publisher.Mono

interface GetProductFacade {
    fun getProduct(productId: Long): Mono<ProductViewResult>

    fun getAllProducts(): Mono<List<ProductViewResult>>
}
