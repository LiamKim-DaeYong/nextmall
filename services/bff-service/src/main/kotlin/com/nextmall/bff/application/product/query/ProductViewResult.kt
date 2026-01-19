package com.nextmall.bff.application.product.query

import com.nextmall.common.util.Money

data class ProductViewResult(
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?,
)
