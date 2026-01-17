package com.nextmall.bff.client.product.request

import com.nextmall.common.util.Money

data class CreateProductClientRequest(
    val name: String,
    val price: Money,
    val stock: Int,
    val sellerId: Long,
    val category: String?,
)
