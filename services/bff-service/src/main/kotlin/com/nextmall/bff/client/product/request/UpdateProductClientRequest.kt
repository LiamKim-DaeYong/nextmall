package com.nextmall.bff.client.product.request

import com.nextmall.common.util.Money

data class UpdateProductClientRequest(
    val name: String,
    val price: Money,
    val stock: Int,
    val category: String?,
)
