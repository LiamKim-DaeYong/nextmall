package com.nextmall.product.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.util.Money
import com.nextmall.product.application.query.ProductView
import com.nextmall.product.application.result.CreateProductResult
import com.nextmall.product.domain.Product
import com.nextmall.product.domain.exception.ProductNotFoundException
import com.nextmall.product.infrastructure.persistence.jooq.ProductJooqRepository
import com.nextmall.product.infrastructure.persistence.jpa.ProductJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val idGenerator: IdGenerator,
    private val productJpaRepository: ProductJpaRepository,
    private val productJooqRepository: ProductJooqRepository,
) {
    @Transactional(readOnly = true)
    fun getProduct(productId: Long): ProductView =
        productJooqRepository.findById(productId)
            ?: throw ProductNotFoundException()

    @Transactional(readOnly = true)
    fun getAllProducts(): List<ProductView> = productJooqRepository.findAll()

    @Transactional
    fun createProduct(
        name: String,
        price: Money,
        stock: Int,
        sellerId: Long,
        category: String?,
    ): CreateProductResult {
        val product =
            Product(
                id = idGenerator.generate(),
                name = name,
                priceAmount = price.amount,
                stockAmount = stock,
                sellerId = sellerId,
                category = category,
            )

        val saved = productJpaRepository.save(product)
        return CreateProductResult(saved)
    }

    @Transactional
    fun updateProduct(
        productId: Long,
        name: String,
        price: Money,
        stock: Int,
        category: String?,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }

        product.update(name, price, stock, category)
    }

    @Transactional
    fun deleteProduct(productId: Long) {
        productJpaRepository.deleteById(productId)
    }
}
