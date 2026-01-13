package com.nextmall.bff.application.product.query

interface GetProductFacade {
    suspend fun getProduct(productId: Long): ProductViewResult

    suspend fun getAllProducts(): List<ProductViewResult>
}
