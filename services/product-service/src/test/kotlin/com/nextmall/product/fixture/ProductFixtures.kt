package com.nextmall.product.fixture

import com.nextmall.product.domain.Product
import java.math.BigDecimal

fun productFixture(
    id: Long = 1L,
    stock: Int = 10,
) =
    Product(
        id = id,
        name = "test-product",
        description = "test-description",
        priceAmount = BigDecimal("10.00"),
        stockAmount = stock,
        sellerId = 10L,
        category = "test-category",
    )
