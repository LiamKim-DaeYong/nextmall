package com.nextmall.bff.presentation.controller

import com.nextmall.bff.application.product.command.CreateProductCommand
import com.nextmall.bff.application.product.command.CreateProductFacade
import com.nextmall.bff.application.product.query.GetProductFacade
import com.nextmall.bff.client.product.ProductServiceClient
import com.nextmall.bff.client.product.request.UpdateProductClientRequest
import com.nextmall.bff.presentation.request.product.CreateProductRequest
import com.nextmall.bff.presentation.request.product.UpdateProductRequest
import com.nextmall.bff.presentation.response.product.CreateProductResponse
import com.nextmall.bff.presentation.response.product.ProductViewResponse
import com.nextmall.bff.presentation.response.product.toResponse
import com.nextmall.common.util.Money
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
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(
    private val createProductFacade: CreateProductFacade,
    private val getProductFacade: GetProductFacade,
    private val productServiceClient: ProductServiceClient,
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

    @PostMapping
    fun createProduct(
        @Valid @RequestBody request: CreateProductRequest,
    ): Mono<ResponseEntity<CreateProductResponse>> {
        val command =
            CreateProductCommand(
                name = request.name,
                price = Money.of(request.price),
                stock = request.stock,
                category = request.category,
            )
        return createProductFacade
            .createProduct(command)
            .map { result ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(CreateProductResponse(result.productId))
            }
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest,
    ): Mono<ResponseEntity<Void>> =
        productServiceClient
            .updateProduct(
                productId,
                UpdateProductClientRequest(
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    stock = request.stock,
                    category = request.category,
                ),
            ).then(Mono.just(ResponseEntity.noContent().build()))

    @PostMapping("/{productId}/suspend")
    fun suspendProduct(
        @PathVariable productId: Long,
    ): Mono<ResponseEntity<Void>> =
        productServiceClient
            .suspendProduct(productId)
            .then(Mono.just(ResponseEntity.noContent().build()))

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable productId: Long,
    ): Mono<ResponseEntity<Void>> =
        productServiceClient
            .deleteProduct(productId)
            .then(Mono.just(ResponseEntity.noContent().build()))
}
