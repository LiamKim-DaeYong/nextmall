package com.nextmall.product.presentation.controller

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.spring.CurrentUser
import com.nextmall.product.application.ProductService
import com.nextmall.product.presentation.response.SellerProductResponse
import com.nextmall.product.presentation.response.toSellerResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sellers/me/products")
class SellerProductController(
    private val productService: ProductService,
) {
    @GetMapping("/{productId}")
    fun getMyProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<SellerProductResponse> {
        val result = productService.getProductForSeller(productId, principal.userIdAsLong())
        return ResponseEntity
            .ok(result.toSellerResponse())
    }

    @GetMapping
    fun getMyProducts(
        @CurrentUser principal: AuthenticatedPrincipal,
    ): ResponseEntity<List<SellerProductResponse>> {
        val results = productService.getAllProductsForSeller(principal.userIdAsLong())
        return ResponseEntity
            .ok(results.map { it.toSellerResponse() })
    }
}
