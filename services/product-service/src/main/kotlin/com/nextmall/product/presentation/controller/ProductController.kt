package com.nextmall.product.presentation.controller

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.spring.CurrentUser
import com.nextmall.common.util.Money
import com.nextmall.product.application.ProductService
import com.nextmall.product.presentation.request.CreateProductRequest
import com.nextmall.product.presentation.request.UpdateProductRequest
import com.nextmall.product.presentation.response.CreateProductResponse
import com.nextmall.product.presentation.response.ProductResponse
import com.nextmall.product.presentation.response.toPublicResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping("/{productId}")
    fun getProduct(@PathVariable productId: Long): ResponseEntity<ProductResponse> {
        val result = productService.getProductForPublic(productId)
        return ResponseEntity
            .ok(result.toPublicResponse())
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val results = productService.getAllProductsForPublic()
        return ResponseEntity
            .ok(results.map { it.toPublicResponse() })
    }

    @PostMapping
    fun createProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @Valid @RequestBody request: CreateProductRequest,
    ): ResponseEntity<CreateProductResponse> {
        val result =
            productService.createProduct(
                name = request.name,
                description = request.description,
                price = Money.of(request.price),
                stock = request.stock,
                sellerId = principal.userIdAsLong(),
                category = request.category,
            )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateProductResponse(result.productId))
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest,
    ): ResponseEntity<Unit> {
        productService.updateProduct(
            productId = productId,
            name = request.name,
            description = request.description,
            price = Money.of(request.price),
            stock = request.stock,
            category = request.category,
            sellerId = principal.userIdAsLong(),
        )
        return ResponseEntity
            .noContent()
            .build()
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.deleteProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{productId}/restore")
    fun restoreProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.restoreProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{productId}/suspend")
    fun suspendProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.suspendProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{productId}/resume")
    fun resumeProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.resumeProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{productId}/show")
    fun showProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.showProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }

    @PostMapping("/{productId}/hide")
    fun hideProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @PathVariable productId: Long,
    ): ResponseEntity<Unit> {
        productService.hideProduct(productId, principal.userIdAsLong())
        return ResponseEntity
            .noContent()
            .build()
    }
}
