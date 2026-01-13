package com.nextmall.bff.client.product

import com.nextmall.bff.client.product.response.ProductViewClientResponse

interface ProductServiceClient {
    suspend fun getProduct(productId: Long): ProductViewClientResponse

    suspend fun getAllProducts(): List<ProductViewClientResponse>
}
