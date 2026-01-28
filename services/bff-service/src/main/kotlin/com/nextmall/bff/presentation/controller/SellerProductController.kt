package com.nextmall.bff.presentation.controller

import com.nextmall.bff.client.product.ProductServiceClient
import com.nextmall.bff.client.product.response.SellerProductViewClientResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/sellers/me/products")
class SellerProductController(
    private val productServiceClient: ProductServiceClient,
) {
    @GetMapping("/{productId}")
    fun getMyProduct(
        @PathVariable productId: Long,
    ): Mono<ResponseEntity<SellerProductViewClientResponse>> =
        productServiceClient
            .getMyProduct(productId)
            .map { ResponseEntity.ok(it) }

    @GetMapping
    fun getMyProducts(): Mono<ResponseEntity<List<SellerProductViewClientResponse>>> =
        productServiceClient
            .getMyProducts()
            .map { ResponseEntity.ok(it) }
}
