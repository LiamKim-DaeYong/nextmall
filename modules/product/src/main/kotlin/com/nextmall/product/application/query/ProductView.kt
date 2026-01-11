package com.nextmall.product.application.query

import com.nextmall.common.util.Money

data class ProductView(
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?
)
