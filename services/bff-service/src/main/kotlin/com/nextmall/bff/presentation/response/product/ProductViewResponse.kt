package com.nextmall.bff.presentation.response.product

import com.nextmall.bff.application.product.query.ProductViewResult
import com.nextmall.common.util.Money

data class ProductViewResponse(
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?,
)

fun ProductViewResult.toResponse() =
    ProductViewResponse(
        id = id,
        name = name,
        price = price,
        stock = stock,
        sellerId = sellerId,
        category = category,
    )
