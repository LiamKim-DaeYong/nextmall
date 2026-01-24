package com.nextmall.product.application

import com.nextmall.common.authorization.exception.AccessDeniedException
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
    fun getProductForPublic(productId: Long): ProductView =
        productJooqRepository.findByIdForPublic(productId)
            ?: throw ProductNotFoundException()

    @Transactional(readOnly = true)
    fun getAllProductsForPublic(): List<ProductView> = productJooqRepository.findAllPublic()

    @Transactional(readOnly = true)
    fun getProductForSeller(
        productId: Long,
        sellerId: Long,
    ): ProductView {
        val product =
            productJooqRepository.findById(productId, includeDeleted = true)
                ?: throw ProductNotFoundException()
        ensureOwner(product.sellerId, sellerId, "read")
        return product
    }

    @Transactional(readOnly = true)
    fun getAllProductsForSeller(
        sellerId: Long,
        includeDeleted: Boolean = true,
    ): List<ProductView> =
        productJooqRepository.findAllBySeller(sellerId, includeDeleted)

    @Transactional
    fun createProduct(
        name: String,
        description: String?,
        price: Money,
        stock: Int,
        sellerId: Long,
        category: String?,
    ): CreateProductResult {
        val product =
            Product(
                id = idGenerator.generate(),
                name = name,
                description = description,
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
        description: String?,
        price: Money,
        stock: Int,
        category: String?,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }

        ensureOwner(product.sellerId, sellerId, "update")
        product.update(name, description, price, stock, category)
    }

    @Transactional
    fun deleteProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "delete")
        product.delete()
    }

    @Transactional
    fun restoreProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "update")
        product.restore()
    }

    @Transactional
    fun suspendProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "update")
        product.suspend()
    }

    @Transactional
    fun resumeProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "update")
        product.resume()
    }

    @Transactional
    fun showProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "update")
        product.show()
    }

    @Transactional
    fun hideProduct(
        productId: Long,
        sellerId: Long,
    ) {
        val product =
            productJpaRepository
                .findById(productId)
                .orElseThrow { ProductNotFoundException() }
        ensureOwner(product.sellerId, sellerId, "update")
        product.hide()
    }

    private fun ensureOwner(
        ownerId: Long,
        sellerId: Long,
        action: String,
    ) {
        if (ownerId != sellerId) {
            throw AccessDeniedException(resource = "product", action = action)
        }
    }
}
