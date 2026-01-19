package com.nextmall.product.infrastructure.persistence.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.common.util.Money
import com.nextmall.jooq.tables.references.PRODUCTS
import com.nextmall.product.application.query.ProductView
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class ProductJooqRepository(
    dsl: DSLContext
) : JooqRepository(dsl) {

    private val fields = arrayOf(
        PRODUCTS.PRODUCT_ID,
        PRODUCTS.NAME,
        PRODUCTS.PRICE,
        PRODUCTS.STOCK,
        PRODUCTS.SELLER_ID,
        PRODUCTS.CATEGORY
    )

    fun findById(productId: Long): ProductView? =
        dsl.select(*fields)
            .from(PRODUCTS)
            .where(PRODUCTS.PRODUCT_ID.eq(productId))
            .fetchOne { it.toProductView() }

    fun findAll(): List<ProductView> =
        dsl.select(*fields)
            .from(PRODUCTS)
            .fetch { it.toProductView() }

    private fun Record.toProductView() =
        ProductView(
            id = getRequired(PRODUCTS.PRODUCT_ID),
            name = getRequired(PRODUCTS.NAME),
            price = Money.of(getRequired(PRODUCTS.PRICE)),
            stock = getRequired(PRODUCTS.STOCK),
            sellerId = getRequired(PRODUCTS.SELLER_ID),
            category = get(PRODUCTS.CATEGORY)
        )
}
