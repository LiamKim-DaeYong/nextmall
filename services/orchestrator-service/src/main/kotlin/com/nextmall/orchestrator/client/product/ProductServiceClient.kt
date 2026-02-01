package com.nextmall.orchestrator.client.product

import com.nextmall.orchestrator.client.product.response.ProductViewClientResponse

interface ProductServiceClient {
    fun getProduct(productId: Long): ProductViewClientResponse
}
