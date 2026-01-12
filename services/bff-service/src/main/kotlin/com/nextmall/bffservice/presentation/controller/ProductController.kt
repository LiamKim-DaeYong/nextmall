package com.nextmall.bffservice.presentation.controller

import com.nextmall.bff.application.product.query.GetProductFacade
import com.nextmall.bffservice.presentation.response.product.ProductViewResponse
import com.nextmall.bffservice.presentation.response.product.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val getProductFacade: GetProductFacade,
) {
    @GetMapping("/{productId}")
    suspend fun getProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<ProductViewResponse> {
        val result = getProductFacade.getProduct(productId)
        return ResponseEntity.ok(result.toResponse())
    }

    @GetMapping
    suspend fun getAllProducts(): ResponseEntity<List<ProductViewResponse>> {
        val results = getProductFacade.getAllProducts()
        return ResponseEntity.ok(results.map { it.toResponse() })
    }
}
