package com.nextmall.bff.presentation.response.product

import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer
import com.nextmall.bff.application.product.query.ProductViewResult
import com.nextmall.common.util.Money

data class ProductViewResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    @JsonSerialize(using = ToStringSerializer::class)
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
