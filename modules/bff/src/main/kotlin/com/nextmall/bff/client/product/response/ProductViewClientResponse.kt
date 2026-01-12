package com.nextmall.bff.client.product.response

import com.nextmall.common.util.Money

data class ProductViewClientResponse(
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?,
)
