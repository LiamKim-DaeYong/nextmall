package com.nextmall.productservice.presentation.controller

import com.nextmall.common.authorization.annotation.RequiresPolicy
import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.spring.CurrentUser
import com.nextmall.common.util.Money
import com.nextmall.product.application.ProductService
import com.nextmall.productservice.presentation.request.CreateProductRequest
import com.nextmall.productservice.presentation.request.UpdateProductRequest
import com.nextmall.productservice.presentation.response.CreateProductResponse
import com.nextmall.productservice.presentation.response.ProductViewResponse
import com.nextmall.productservice.presentation.response.toResponse
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
    fun getProduct(@PathVariable productId: Long): ResponseEntity<ProductViewResponse> {
        val result = productService.getProduct(productId)

        return ResponseEntity
            .ok(result.toResponse())
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductViewResponse>> {
        val results = productService.getAllProducts()

        return ResponseEntity
            .ok(results.map { it.toResponse() })
    }

    @PostMapping
    @RequiresPolicy(resource = "product", action = "create")
    fun createProduct(
        @CurrentUser principal: AuthenticatedPrincipal,
        @Valid @RequestBody request: CreateProductRequest
    ): ResponseEntity<CreateProductResponse> {
        val result = productService.createProduct(
            name = request.name,
            price = Money.of(request.price),
            stock = request.stock,
            sellerId = principal.userIdAsLong(),
            category = request.category
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateProductResponse(result.productId))
    }

    @PutMapping("/{productId}")
    @RequiresPolicy(resource = "product", action = "update", resourceIdParam = "productId")
    fun updateProduct(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<Unit> {
        productService.updateProduct(
            productId = productId,
            name = request.name,
            price = Money.of(request.price),
            stock = request.stock,
            category = request.category
        )

        return ResponseEntity
            .noContent()
            .build()
    }

    @DeleteMapping("/{productId}")
    @RequiresPolicy(resource = "product", action = "delete", resourceIdParam = "productId")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Unit> {
        productService.deleteProduct(productId)

        return ResponseEntity
            .noContent()
            .build()
    }
}
