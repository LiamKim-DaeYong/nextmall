package com.nextmall.bff.application.product.query

import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component

@Component
class GetProductFacadeImpl(
    private val productServiceClient: ProductServiceClient,
) : GetProductFacade {
    override suspend fun getProduct(productId: Long): ProductViewResult {
        val product = productServiceClient.getProduct(productId)
        return ProductViewResult(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = product.stock,
            sellerId = product.sellerId,
            category = product.category,
        )
    }

    override suspend fun getAllProducts(): List<ProductViewResult> =
        productServiceClient.getAllProducts().map {
            ProductViewResult(
                id = it.id,
                name = it.name,
                price = it.price,
                stock = it.stock,
                sellerId = it.sellerId,
                category = it.category,
            )
        }
}
