package com.nextmall.productservice.presentation.response

import com.nextmall.common.util.Money
import com.nextmall.product.application.query.ProductView

data class ProductViewResponse(
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?
)

fun ProductView.toResponse() =
    ProductViewResponse(
        id = id,
        name = name,
        price = price,
        stock = stock,
        sellerId = sellerId,
        category = category
    )
