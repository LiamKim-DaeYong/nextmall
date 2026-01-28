package com.nextmall.bff.application.product.command

import com.nextmall.bff.client.product.ProductServiceClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateProductFacade(
    private val productServiceClient: ProductServiceClient,
) {
    /**
     * 상품 생성을 위해 product-service를 호출한다.
     */
    fun createProduct(command: CreateProductCommand): Mono<CreateProductResult> =
        productServiceClient
            .createProduct(
                name = command.name,
                price = command.price.amount,
                stock = command.stock,
                category = command.category,
            ).map { response -> CreateProductResult(productId = response.productId) }
}
