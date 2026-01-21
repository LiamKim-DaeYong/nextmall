package com.nextmall.bff.application.product.query

import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetProductFacadeImpl(
    private val productServiceClient: ProductServiceClient,
) : GetProductFacade {
    override fun getProduct(productId: Long): Mono<ProductViewResult> =
        productServiceClient
            .getProduct(productId)
            .map { product ->
                ProductViewResult(
                    id = product.id,
                    name = product.name,
                    price = product.price,
                    stock = product.stock,
                    sellerId = product.sellerId,
                    category = product.category,
                )
            }

    override fun getAllProducts(): Mono<List<ProductViewResult>> =
        productServiceClient
            .getAllProducts()
            .map { products ->
                products.map {
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
}
