package com.nextmall.bff.presentation.controller

import com.nextmall.bff.client.product.ProductServiceClient
import com.nextmall.bff.presentation.response.product.SellerProductViewResponse
import com.nextmall.bff.presentation.response.product.toResponse
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
    ): Mono<ResponseEntity<SellerProductViewResponse>> =
        productServiceClient
            .getMyProduct(productId)
            .map { ResponseEntity.ok(it.toResponse()) }

    @GetMapping
    fun getMyProducts(): Mono<ResponseEntity<List<SellerProductViewResponse>>> =
        productServiceClient
            .getMyProducts()
            .map { ResponseEntity.ok(it.map { response -> response.toResponse() }) }
}
