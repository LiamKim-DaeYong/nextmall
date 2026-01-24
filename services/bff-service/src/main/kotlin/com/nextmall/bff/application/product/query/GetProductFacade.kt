package com.nextmall.bff.application.product.query

import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetProductFacade(
    private val productServiceClient: ProductServiceClient,
) {
    /**
     * 상품 단건을 조회해 BFF 응답 모델로 변환한다.
     */
    fun getProduct(productId: Long): Mono<ProductViewResult> =
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

    /**
     * 상품 목록을 조회해 BFF 응답 모델로 변환한다.
     */
    fun getAllProducts(): Mono<List<ProductViewResult>> =
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
