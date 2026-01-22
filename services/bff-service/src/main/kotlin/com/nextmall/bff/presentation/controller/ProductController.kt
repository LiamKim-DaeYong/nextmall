package com.nextmall.bff.presentation.controller

import com.nextmall.bff.application.product.query.GetProductFacade
import com.nextmall.bff.presentation.response.product.ProductViewResponse
import com.nextmall.bff.presentation.response.product.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(
    private val getProductFacade: GetProductFacade,
) {
    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): Mono<ResponseEntity<ProductViewResponse>> =
        getProductFacade
            .getProduct(productId)
            .map { result -> ResponseEntity.ok(result.toResponse()) }

    @GetMapping
    fun getAllProducts(): Mono<ResponseEntity<List<ProductViewResponse>>> =
        getProductFacade
            .getAllProducts()
            .map { results -> ResponseEntity.ok(results.map { it.toResponse() }) }
}
