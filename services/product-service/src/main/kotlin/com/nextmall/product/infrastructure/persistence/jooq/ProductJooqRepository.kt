package com.nextmall.product.infrastructure.persistence.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.conditionList
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.common.util.Money
import com.nextmall.jooq.tables.references.PRODUCTS
import com.nextmall.product.application.query.ProductView
import com.nextmall.product.domain.DisplayStatus
import com.nextmall.product.domain.SaleStatus
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
        PRODUCTS.DESCRIPTION,
        PRODUCTS.PRICE,
        PRODUCTS.STOCK,
        PRODUCTS.SALE_STATUS,
        PRODUCTS.DISPLAY_STATUS,
        PRODUCTS.SELLER_ID,
        PRODUCTS.CATEGORY,
        PRODUCTS.IS_DELETED,
        PRODUCTS.CREATED_AT,
    )

    fun findById(
        productId: Long,
        includeDeleted: Boolean,
    ): ProductView? =
        dsl.select(*fields)
            .from(PRODUCTS)
            .where(
                conditionList(
                    PRODUCTS.PRODUCT_ID.eq(productId),
                    if (includeDeleted) null else PRODUCTS.IS_DELETED.eq(false),
                ),
            )
            .fetchOne { it.toProductView() }

    fun findByIdForPublic(productId: Long): ProductView? =
        dsl.select(*fields)
            .from(PRODUCTS)
            .where(
                PRODUCTS.PRODUCT_ID.eq(productId),
                PRODUCTS.IS_DELETED.eq(false),
                PRODUCTS.SALE_STATUS.`in`(SaleStatus.ON_SALE.name, SaleStatus.SOLD_OUT.name),
                PRODUCTS.DISPLAY_STATUS.eq(DisplayStatus.VISIBLE.name),
            )
            .fetchOne { it.toProductView() }

    fun findAllPublic(): List<ProductView> =
        dsl.select(*fields)
            .from(PRODUCTS)
            .where(
                PRODUCTS.IS_DELETED.eq(false),
                PRODUCTS.SALE_STATUS.`in`(SaleStatus.ON_SALE.name, SaleStatus.SOLD_OUT.name),
                PRODUCTS.DISPLAY_STATUS.eq(DisplayStatus.VISIBLE.name),
            )
            .orderBy(PRODUCTS.CREATED_AT.desc())
            .fetch { it.toProductView() }

    fun findAllBySeller(
        sellerId: Long,
        includeDeleted: Boolean,
    ): List<ProductView> =
        dsl.select(*fields)
            .from(PRODUCTS)
            .where(
                conditionList(
                    PRODUCTS.SELLER_ID.eq(sellerId),
                    if (includeDeleted) null else PRODUCTS.IS_DELETED.eq(false),
                ),
            )
            .orderBy(PRODUCTS.CREATED_AT.desc())
            .fetch { it.toProductView() }

    private fun Record.toProductView() =
        ProductView(
            id = getRequired(PRODUCTS.PRODUCT_ID),
            name = getRequired(PRODUCTS.NAME),
            description = this[PRODUCTS.DESCRIPTION],
            price = Money.of(getRequired(PRODUCTS.PRICE)),
            stock = getRequired(PRODUCTS.STOCK),
            saleStatus = SaleStatus.valueOf(getRequired(PRODUCTS.SALE_STATUS)),
            displayStatus = DisplayStatus.valueOf(getRequired(PRODUCTS.DISPLAY_STATUS)),
            sellerId = getRequired(PRODUCTS.SELLER_ID),
            category = get(PRODUCTS.CATEGORY),
            isDeleted = getRequired(PRODUCTS.IS_DELETED),
            createdAt = getRequired(PRODUCTS.CREATED_AT).toInstant(),
        )
}
